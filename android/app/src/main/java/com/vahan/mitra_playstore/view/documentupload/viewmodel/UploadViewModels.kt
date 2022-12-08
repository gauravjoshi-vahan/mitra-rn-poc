package com.vahan.mitra_playstore.view.documentupload.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.vahan.mitra_playstore.model.CashOutModel
import com.vahan.mitra_playstore.models.VerificationResponseModel
import com.vahan.mitra_playstore.network.RetrofitClient
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.view.BaseApplication
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull

import okhttp3.MultipartBody

import okhttp3.OkHttpClient

import okhttp3.Request

import okhttp3.RequestBody


const val FRONT_AADHAR = 0
const val BACK_AADHAR = 1
const val PAN = 2
const val FRONT_DL = 3
const val BACK_DL = 4

class UploadViewModels : ViewModel() {
    var imagePath = ""
    var frontAdhaarCard = ""
    var backAdhaarCard = ""
    var frontDLCard = ""
    var backDLCard = ""
    var bitmapDataPAN: Bitmap? = null
    var bitmapDataAADHARFRONT: Bitmap? = null
    var bitmapDataAADHARBACK: Bitmap? = null
    var bitmapDataDLFront: Bitmap? = null
    var bitmapDataDLBack: Bitmap? = null
    var mutableLiveUpload: MutableLiveData<String> = MutableLiveData<String>()
    var mutableVerifyPhoto: MutableLiveData<VerificationResponseModel> = MutableLiveData<VerificationResponseModel>()


    fun createNewFile(parentDir: File?, idType: String, FrontFromAadhaarOrDL: String) = File(parentDir, "IMG_${System.nanoTime()}").apply {
        if (idType == "AADHAAR_CARD" ){
            if (FrontFromAadhaarOrDL=="AADHAAR_CARD_F") {
                frontAdhaarCard = absolutePath.toString()
            } else {
                backAdhaarCard = absolutePath.toString()
            }
        }else if(idType == "DRIVING_LICENSE") {
            if (FrontFromAadhaarOrDL=="DRIVING_LICENCE_CARD_F") {
                frontDLCard = absolutePath.toString()
            } else {
                backDLCard = absolutePath.toString()
            }
        } else {
            imagePath = absoluteFile.toString()
        }
    }

    fun createBitMap(uri: Uri, type: Int): Bitmap {
        return when (type) {
            PAN -> {
                bitmapDataPAN = MediaStore.Images.Media.getBitmap(BaseApplication.context?.contentResolver, uri) as Bitmap
                bitmapDataPAN as Bitmap
            }
            FRONT_AADHAR -> {
                bitmapDataAADHARFRONT = MediaStore.Images.Media.getBitmap(BaseApplication.context?.contentResolver, uri) as Bitmap
                bitmapDataAADHARFRONT as Bitmap
            }
            BACK_AADHAR -> {
                bitmapDataAADHARBACK = MediaStore.Images.Media.getBitmap(BaseApplication.context?.contentResolver, uri) as Bitmap
                bitmapDataAADHARBACK as Bitmap
            }
            FRONT_DL -> {
                bitmapDataDLFront = MediaStore.Images.Media.getBitmap(BaseApplication.context?.contentResolver, uri) as Bitmap
                bitmapDataDLFront as Bitmap
            }
            BACK_DL -> {
                bitmapDataDLBack = MediaStore.Images.Media.getBitmap(BaseApplication.context?.contentResolver, uri) as Bitmap
                bitmapDataDLBack as Bitmap
            }
            else -> {null as Bitmap}
        }
    }

    fun uploadImageFile(
        uploadActivity: Context,
        token: String?,
        data: Bitmap,
        filename: String
    ): MutableLiveData<String>? {
        try {
            val f = File(uploadActivity.cacheDir, "$filename.jpg")
            f.createNewFile()
            val baos = ByteArrayOutputStream()
            data.compress(Bitmap.CompressFormat.PNG, 50, baos)
            val b = baos.toByteArray()
            val fos = FileOutputStream(f)
            fos.write(b)
            fos.flush()
            fos.close()

            var client: OkHttpClient = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .build()
            val requestBody: RequestBody =MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file", f.toString(),
                    RequestBody.create("image/png".toMediaTypeOrNull(), f)
                )
                .build()
            val request: Request = Request.Builder()
                .post(requestBody)
                .url("https://api.mitra.vahan.co/upload")
                //.url("https://stg-api.mitra.vahan.co/upload")
                .addHeader("Timestamp", System.currentTimeMillis().toString())
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Authorization", token!!)
                .addHeader("app-build", "signed")
                .addHeader("device_id", PrefrenceUtils.retriveData(uploadActivity, Constants.DEVICE_ID))
                .build()
            val call = client.newCall(request)
            call.enqueue(object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: IOException) {}

                @Throws(IOException::class)
                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    if (response.isSuccessful) {
                        try {
                            val json = JSONObject(response.body!!.string())
                            mutableLiveUpload.postValue(json.getString("fileURL"))
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    } else {
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return mutableLiveUpload
    }

    fun verifyAadharCard(
        context: Context?,
        source: JsonObject?
    ): MutableLiveData<VerificationResponseModel>? {
        val call = RetrofitClient().apiRetrofitInterceptor.verifyAadharCard(source)
        call.enqueue(object : Callback<VerificationResponseModel?> {
            override fun onResponse(
                call: Call<VerificationResponseModel?>,
                response: Response<VerificationResponseModel?>
            ) {
                try {
                    val responseModel = VerificationResponseModel()
                    if (response.code() in 200..299) {
                        mutableVerifyPhoto.postValue(response.body())
                    } else if (response.code() in 400..499) {
                        responseModel.statusCode = response.code()
                        mutableVerifyPhoto.postValue(responseModel)
                    } else if (response.code() >= 500) {
                        responseModel.statusCode = 500
                        mutableVerifyPhoto.postValue(responseModel)
                    }
                } catch (e: Exception) {
                    val responseModel = VerificationResponseModel()
                    responseModel.statusCode = 500
                    mutableVerifyPhoto.postValue(responseModel)
                }
            }

            override fun onFailure(call: Call<VerificationResponseModel?>, t: Throwable) {
                PrefrenceUtils.insertData(context, Constants.AADHARCARDFRONT, "")
                PrefrenceUtils.insertData(context, Constants.AADHARCARDBACK, "")
                val responseModel = VerificationResponseModel()
                responseModel.statusCode = 500
                mutableVerifyPhoto.postValue(responseModel)
            }
        })
        return mutableVerifyPhoto
    }


}