package com.vahan.mitra_playstore.workmanager

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.vahan.mitra_playstore.BuildConfig
import com.vahan.mitra_playstore.network.RetrofitClient
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.view.BaseApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationWorkerManager(context: Context,
workParams : WorkerParameters) : Worker(context,workParams) {

    override fun doWork(): Result {
        val jsonObject = JsonObject()
        jsonObject.addProperty("jwtToken", inputData.getString("jwtToken"))
        jsonObject.addProperty("mobileNumber",
            PrefrenceUtils.retriveData(
            BaseApplication.context,
            Constants.PHONENUMBER)
        )
        jsonObject.addProperty("appVersion", BuildConfig.VERSION_NAME)
        val jsonArray = JsonArray()
        val smallJsonObject = JsonObject()
        smallJsonObject.addProperty("message", inputData.getString("receivedMessage"))
        smallJsonObject.addProperty("packageName", inputData.getString("receivedPackage"))
        smallJsonObject.addProperty("receivedAt", inputData.getString("timestamp"))
        jsonArray.add(smallJsonObject)
        jsonObject.add("notification", jsonArray)
        val call = RetrofitClient().apiRetrofitInterceptor.postNotificationInfo(jsonObject)
        call.enqueue(object : Callback<Void?> {
            override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                try {
                    if (response.isSuccessful) {
                        Log.d("APICALL", "onResponseS: ")                        }
                } catch (e: Exception) {
                    Log.d("APICALL", "onResponseF: ")

                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Log.d("APICALL", "onResponseFA: ")                }
        })
        return Result.success()
    }
}




