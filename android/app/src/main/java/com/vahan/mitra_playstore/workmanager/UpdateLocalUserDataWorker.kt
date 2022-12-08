package com.vahan.mitra_playstore.workmanager

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.view.MainActivity

class UpdateLocalUserDataWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    @SuppressLint("RestrictedApi")
    override fun doWork(): Result {
        return if (PrefrenceUtils.retriveDataInBoolean(
                applicationContext,
                Constants.UPDATE_LOCAL_USER_DATA
            )
        ) {
            // Utilities.postDataOnServer("yes","workmanager")
            MainActivity.instance?.postDataOnServer("yes", "workmanager")
            Result.success()
        } else {
            Result.Retry()
        }
    }
}