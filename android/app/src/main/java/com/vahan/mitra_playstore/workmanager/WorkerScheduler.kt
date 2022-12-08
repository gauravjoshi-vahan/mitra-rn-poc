package com.vahan.mitra_playstore.workmanager

import android.content.Context
import androidx.work.*
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.view.BaseApplication.Companion.context
import java.util.concurrent.TimeUnit

object WorkerScheduler {
    //define constraints
    // public static Context context = null;
    fun updateUserLocation(lat: String?, lng: String?) {
        val myConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val data = Data.Builder()
        data.putString(Constants.LAT, lat)
        data.putString(Constants.LNG, lng)
        val location =
            OneTimeWorkRequest.Builder(UpdateLocationWorker::class.java)
                .setConstraints(myConstraints)
                .setInputData(data.build())
                .addTag(Constants.UPDATELOCATION)
                .build()
        try {
            context?.let { WorkManager.getInstance(it).enqueue(location) }
        } catch (exception: NullPointerException) {
            exception.printStackTrace()
        }
    }

    fun updateUserSMS(context: Context, mobile: String, jwtToken: String) {
        val myConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val data = Data.Builder()
        data.putString("mobile", mobile)
        data.putString("jwtToken", jwtToken)
        val periodicWorkRequest = OneTimeWorkRequest.Builder(SMSWorkerManger::class.java)
            .setConstraints(myConstraints)
            .setInputData(data.build())
            .addTag("updateUserSms")
            .build()
        try {
            context.let { WorkManager.getInstance(it).enqueue(periodicWorkRequest) }
        } catch (exception: NullPointerException) {
            exception.printStackTrace()
        }
    }

    fun updateDeviceInfo(context: Context, mobile: String, jwtToken: String) {
        val myConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val data = Data.Builder()
        data.putString("mobile", mobile)
        data.putString("jwtToken", jwtToken)
        val periodicWorkRequest =
            OneTimeWorkRequest.Builder(DeviceInfoWorkerManager::class.java)
                .setConstraints(myConstraints)
                .setInputData(data.build())
                .setConstraints(myConstraints).build()
        try {
            WorkManager.getInstance(context).enqueue(periodicWorkRequest)
        } catch (exception: NullPointerException) {
            exception.printStackTrace()
        }
    }

    fun noUser(context: Context, mobile: String, jwtToken: String) {
        val myConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val data = Data.Builder()
        data.putString("mobile", mobile)
        data.putString("jwtToken", jwtToken)
        val periodicWorkRequest =
            OneTimeWorkRequest.Builder(NoUser::class.java)
                .setConstraints(myConstraints)
                .setInputData(data.build())
                .setConstraints(myConstraints).build()
        try {
            context.let {
                WorkManager.getInstance(it).enqueue(periodicWorkRequest)
            }
        } catch (exception: NullPointerException) {
            exception.printStackTrace()
        }
    }

    fun updateUserNotification(
        context: Context,
        mobile: String,
        jwtToken: String,
        receivedMessage: String,
        receivedPackage: String,
        timeStamp : String
    ) {
        val myConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val data = Data.Builder()
        data.putString("mobile", mobile)
        data.putString("jwtToken", jwtToken)
        data.putString("receivedMessage", receivedMessage)
        data.putString("receivedPackage", receivedPackage)
        data.putString("timestamp", timeStamp)
        val periodicWorkRequest =
            OneTimeWorkRequest.Builder(NotificationWorkerManager::class.java)
                .setConstraints(myConstraints)
                .setInputData(data.build())
                .setConstraints(myConstraints).build()
        try {
            context.let {
                WorkManager.getInstance(it).enqueue(periodicWorkRequest)
            }
        } catch (exception: NullPointerException) {
            exception.printStackTrace()
        }
    }
}