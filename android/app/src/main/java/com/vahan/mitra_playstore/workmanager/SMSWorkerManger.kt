package com.vahan.mitra_playstore.workmanager

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.moengage.BuildConfig
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

class SMSWorkerManger(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    private val contextM = context
    var listOfAllSMS: ArrayList<LocaInfoSMSModel> = ArrayList()
    private var checkSmsCount = 8000

    @SuppressLint("Recycle")
    override fun doWork(): Result {
        val mSmsQueryUri = Uri.parse("content://sms/inbox")
        var cursor: Cursor? = null
        if (PrefrenceUtils.retriveData(contextM, "timestamp").equals("")) {
            try {
                cursor = contextM.contentResolver.query(
                    mSmsQueryUri,
                    null, null, null, null
                )

                if (cursor == null) {
                    Log.i("CONTACT_LIST", "cursor is null. uri: \$mSmsQueryUri")
                }
                if (cursor != null) {
                    var hasData = cursor.moveToFirst()
                    while (hasData && checkSmsCount >= 0) {
                        listOfAllSMS.add(
                            LocaInfoSMSModel(
                                cursor.getString(cursor.getColumnIndexOrThrow("address")),
                                createAt(System.currentTimeMillis()),
                                cursor.getString(cursor.getColumnIndexOrThrow("body")),
                                createAt(cursor.getLong(cursor.getColumnIndexOrThrow("date")))
                            )
                        )
                        checkSmsCount--;
                        hasData = cursor.moveToNext()
                    }
                }
                PrefrenceUtils.insertData(contextM, "timestamp", listOfAllSMS[0].receivedAt)
            } catch (e: Exception) {
                Log.e("CONTACT_LIST", e.message!!)
            } finally {
                cursor!!.close()
            }
        } else {
            try {
                cursor = contextM.contentResolver.query(
                    mSmsQueryUri,
                    null, null, null, null
                )

                if (cursor == null) {
                    Log.i("CONTACT_LIST", "cursor is null. uri: \$mSmsQueryUri")
                }
                if (cursor != null) {
                    listOfAllSMS.clear()
                    var hasData = cursor.moveToFirst()
                    while (hasData) {
                        if (compareTwoDate(
                                PrefrenceUtils.retriveData(contextM, "timestamp"),
                                cursor.getLong(cursor.getColumnIndexOrThrow("date"))
                            )
                        ) {
                            listOfAllSMS.add(
                                LocaInfoSMSModel(
                                    cursor.getString(cursor.getColumnIndexOrThrow("address")),
                                    createAt(System.currentTimeMillis()),
                                    cursor.getString(cursor.getColumnIndexOrThrow("body")),
                                    createAt(cursor.getLong(cursor.getColumnIndexOrThrow("date")))
                                )
                            )
                            PrefrenceUtils.insertData(
                                contextM,
                                "timestamp",
                                listOfAllSMS[0].receivedAt
                            )
                            hasData = cursor.moveToNext()
                        } else {
                            hasData = false
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("CONTACT_LIST", e.message!!)
            } finally {
                cursor!!.close()
            }
        }

        val mobile = inputData.getString("mobile")
        val token = inputData.getString("jwtToken")
        val jsonObject = JsonObject()
        jsonObject.addProperty("jwtToken", token)
        jsonObject.addProperty("mobileNumber", PrefrenceUtils.retriveData(
            BaseApplication.context,
            Constants.PHONENUMBER))
        jsonObject.addProperty("appVersion", com.vahan.mitra_playstore.BuildConfig.VERSION_NAME)
        val jsonArray = JsonArray()
        for (i in 0 until listOfAllSMS.size) {
            val smallJsonObject = JsonObject()
            smallJsonObject.addProperty("receivedAt", listOfAllSMS[i].receivedAt)
            smallJsonObject.addProperty("senderAddress", listOfAllSMS[i].senderAddress)
            smallJsonObject.addProperty("text", listOfAllSMS[i].text)
            smallJsonObject.addProperty("updateAt", listOfAllSMS[i].updateAt)
            jsonArray.add(smallJsonObject)
        }
        jsonObject.add("sms", jsonArray)
        if (listOfAllSMS.size > 0) {
            val call = RetrofitClient().apiRetrofitInterceptor.postSMSInfo(jsonObject)
            call.enqueue(object : Callback<Void?> {
                override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                    try {
                        if (response.isSuccessful) {
                            WorkManager.getInstance(contextM).cancelAllWorkByTag("updateUserSms")
                        }
                    } catch (e: Exception) {
                        WorkManager.getInstance(contextM).cancelAllWorkByTag("updateUserSms")
                    }
                }

                override fun onFailure(call: Call<Void?>, t: Throwable) {
                    WorkManager.getInstance(contextM).cancelAllWorkByTag("updateUserSms")
                }
            })
        }

        return Result.success()
    }

    @SuppressLint("SimpleDateFormat")
    private fun compareTwoDate(retriveData: String?, long: Long): Boolean {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        simpleDateFormat.timeZone = TimeZone.getTimeZone("GMT")
        val specifiedDate = simpleDateFormat.parse(retriveData.toString())
        val afterDateS = simpleDateFormat.format(Date(long))
        val afterDate = simpleDateFormat.parse(afterDateS)
        if (afterDate != null) {
            if (afterDate.after(specifiedDate)) {
                return true
            }
        }
        return false
        // In between
    }

    @SuppressLint("SimpleDateFormat")
    private fun createAt(currentTimeMillis: Long): String {
        val simpleDateFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        simpleDateFormat.timeZone = TimeZone.getTimeZone("GMT")
        return simpleDateFormat.format(Date(currentTimeMillis))
    }
}
