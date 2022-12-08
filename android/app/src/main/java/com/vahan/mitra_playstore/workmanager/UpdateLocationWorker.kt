package com.vahan.mitra_playstore.workmanager

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.provider.ContactsContract
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.vahan.mitra_playstore.R
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.view.BaseApplication.Companion.context
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet


class UpdateLocationWorker(context: Context, workerParams: WorkerParameters) :
  Worker(context, workerParams) {
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    @SuppressLint("SimpleDateFormat")
    override fun doWork(): Result {
        val lat = inputData.getString(Constants.LAT)
        val lng = inputData.getString(Constants.LNG)
        val hashMap = HashMap<String, String>()
        hashMap[Constants.LATTITUDE] = lat.toString()
        hashMap[Constants.LONGITUDE] = lng.toString()
        val s = SimpleDateFormat(context?.getString(R.string.date_format))
        val format: String = s.format(Date())
        hashMap["created on"] = format
        val locationContainer: HashMap<String, String> = HashMap()
        locationContainer[lat!!] = lng!!
        val locationData: HashSet<HashMap<String, String>> = HashSet()
        locationData.add(locationContainer)
        val handler = Handler(Looper.getMainLooper())
        if (locationData.size > 0) {
            handler.postDelayed({ // Run your task here
                Log.d("Message",
                    "initView: ${PrefrenceUtils.retriveData(context, Constants.FIREBASE_TOKEN)}")
                try {
                    db.collection("update_location")
                        .document(
                            PrefrenceUtils.retriveData(context, Constants.PHONENUMBER)
                        ).set(hashMap)
                        .addOnSuccessListener {
                            Log.d("location", "Location added: $lat")
                        }
                        .addOnFailureListener { e ->
                            Log.d("location", "Location Erroradded: $e")
                        }
                } catch (e: Exception) {

                }
            }, 3000)
            return Result.success()
        } else {
            return Result.failure()
        }
    }

}