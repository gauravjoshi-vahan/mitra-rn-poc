package com.vahan.mitra_playstore.workmanager

import android.content.Context
import android.os.Build
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.JsonObject
import com.vahan.mitra_playstore.network.RetrofitClient
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.view.BaseApplication
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeviceInfoWorkerManager(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    private val contextM = context

    override fun doWork(): Result {
        val deviceInfo = getSystemDetail();
        val call = RetrofitClient().apiRetrofitInterceptor.postDeviceInfo(deviceInfo)
        call.enqueue(object : Callback<Void?> {
            override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                try {
                    if (response.isSuccessful) {
                        WorkManager.getInstance(contextM).cancelAllWorkByTag("updateDeviceInfo")
                    }
                } catch (e: Exception) {
                    WorkManager.getInstance(contextM).cancelAllWorkByTag("updateDeviceInfo")
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                WorkManager.getInstance(contextM).cancelAllWorkByTag("updateDeviceInfo")
            }
        })
        return Result.success()
    }

    private fun getSystemDetail(): JsonObject {
        //confirm this
        val contentResolver = applicationContext.contentResolver;
        val jsonObject = JsonObject();
        jsonObject.addProperty("brand", Build.BRAND)
        jsonObject.addProperty("base", Build.VERSION_CODES.BASE)
        jsonObject.addProperty("board", Build.BOARD)
        jsonObject.addProperty("fingerprint", Build.FINGERPRINT)
        jsonObject.addProperty("host", Build.HOST)
        jsonObject.addProperty("id", Build.ID)
        jsonObject.addProperty("incremental", Build.VERSION.INCREMENTAL)
        jsonObject.addProperty("manufacturer", Build.MANUFACTURER)
        jsonObject.addProperty("mobileNumber", PrefrenceUtils.retriveData(
            BaseApplication.context,
            Constants.PHONENUMBER))
        jsonObject.addProperty("model", Build.MODEL)
        jsonObject.addProperty("sdk", Build.VERSION.SDK_INT)
        jsonObject.addProperty("type", Build.TYPE)
        jsonObject.addProperty("user", Build.USER)
        jsonObject.addProperty("versionCode", Build.VERSION.RELEASE)
        return jsonObject;
    }
}
