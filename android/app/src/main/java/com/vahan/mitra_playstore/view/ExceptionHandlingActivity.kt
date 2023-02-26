package com.vahan.mitra_playstore.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Process
//import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.view.MainActivity
import kotlin.system.exitProcess

class ExceptionHandlingActivity(private val activity: Activity, private val context: Context) :
    Thread.UncaughtExceptionHandler {
    @SuppressLint("UnspecifiedImmutableFlag")
    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        try {
//            FirebaseCrashlytics.getInstance().recordException(throwable);
//            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
        if (PrefrenceUtils.retriveData(
                activity.baseContext,
                Constants.API_TOKEN
            ) != ""
        ) {
            val intent = Intent(activity, MainActivity::class.java)
            intent.putExtra(Constants.CRASH, true)
            intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
                        or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        or Intent.FLAG_ACTIVITY_NEW_TASK
            )
            context.startActivity(intent)
            Process.killProcess(Process.myPid())
            exitProcess(0)
        } else {
            val intent = Intent(activity.baseContext, SplashActivity::class.java)
            intent.putExtra(Constants.CRASH, true)
            intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
                        or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        or Intent.FLAG_ACTIVITY_NEW_TASK
            )
            context.startActivity(intent)
            Process.killProcess(Process.myPid())
            exitProcess(0)
        }
    }

    init {
//        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
    }
}