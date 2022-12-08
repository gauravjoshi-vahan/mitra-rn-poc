package com.vahan.mitra_playstore.workmanager

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.vahan.mitra_playstore.models.LocaInfoSMSModel
import com.vahan.mitra_playstore.network.RetrofitClient
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.view.BaseApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class NoUser(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    private val contextM = context


    @SuppressLint("Recycle")
    override fun doWork(): Result {
        val mobile = inputData.getString("mobile")
        val token = inputData.getString("jwtToken")
        val jsonObject = JsonObject()
        jsonObject.addProperty("jwtToken", token)
        jsonObject.addProperty("mobileNumber", PrefrenceUtils.retriveData(BaseApplication.context,Constants.PHONENUMBER))
        jsonObject.addProperty("appVersion", com.vahan.mitra_playstore.BuildConfig.VERSION_NAME)
        val jsonArray = JsonArray()
        jsonObject.add("sms", jsonArray)

            val call = RetrofitClient().apiRetrofitInterceptor.postSMSInfo(jsonObject)
            call.enqueue(object : Callback<Void?> {
                override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                    try {
                        if (response.isSuccessful) {
                            WorkManager.getInstance(contextM).cancelAllWorkByTag("noUser")
                        }
                    } catch (e: Exception) {
                        WorkManager.getInstance(contextM).cancelAllWorkByTag("noUser")
                    }
                }

                override fun onFailure(call: Call<Void?>, t: Throwable) {
                    WorkManager.getInstance(contextM).cancelAllWorkByTag("noUser")
                }
            })


        return Result.success()
    }

}
