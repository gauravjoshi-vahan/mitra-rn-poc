package com.vahan.mitra_playstore.view.documentupload.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.vahan.mitra_playstore.models.VerificationResponseModel
import com.vahan.mitra_playstore.network.RetrofitClient
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.utils.PrefrenceUtils
import com.vahan.mitra_playstore.view.BaseApplication
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
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

const val FRONT_AADHAR = 0
const val BACK_AADHAR = 1
const val PAN = 2
const val FRONT_DL = 3
const val BACK_DL = 4
const val PROFILE_PIC = 5
const val FRONT_RC = 6
const val BACK_RC = 7
const val BANK_DOC = 8

class UploadViewModels : ViewModel() {
    var imagePath = ""
    var profilePicImagePath = ""
    var frontAdhaarCard = ""
    var backAdhaarCard = ""
    var frontDLCard = ""
    var backDLCard = ""
    var frontRC = ""
    var backRC = ""
    var bankDoc = ""
    var bitmapDataProfilePic: Bitmap? = null
    var bitmapDataPAN: Bitmap? = null
    var bitmapDataAADHARFRONT: Bitmap? = null
    var bitmapDataAADHARBACK: Bitmap? = null
    var bitmapDataDLFront: Bitmap? = null
    var bitmapDataDLBack: Bitmap? = null
    var bitmapDataRCFront: Bitmap? = null
    var bitmapDataRCBack: Bitmap? = null
    var bitmapDataBankDoc: Bitmap? = null
    var mutableLiveUpload: MutableLiveData<String> = MutableLiveData<String>()
    var mutableVerifyPhoto: MutableLiveData<VerificationResponseModel> =
        MutableLiveData<VerificationResponseModel>()
    var disableAllUploadButtons: Boolean = false
    var inputPanNumber = ""
    var inputDLNumber = ""


    fun createNewFile(parentDir: File?, idType: String, FrontFromAadhaarOrDL: String) =
        File(parentDir, "IMG_${System.nanoTime()}").apply {
            if (idType == "AADHAAR_CARD") {
                if (FrontFromAadhaarOrDL == "AADHAAR_CARD_F") {
                    frontAdhaarCard = absolutePath.toString()
                } else {
                    backAdhaarCard = absolutePath.toString()
                }
            } else if (idType == "DRIVING_LICENSE") {
                if (FrontFromAadhaarOrDL == "DRIVING_LICENCE_CARD_F") {
                    frontDLCard = absolutePath.toString()
                } else {
                    backDLCard = absolutePath.toString()
                }
            } else if (idType == "VEHICLE_RC") {
                if (FrontFromAadhaarOrDL == "VEHICLE_RC_F") {
                    frontRC = absolutePath.toString()
                } else {
                    backRC = absolutePath.toString()
                }
            } else if (idType == "BANK_DOC") {
                bankDoc = absoluteFile.toString()
            } else if (idType == "PROFILE_PIC") {
                profilePicImagePath = absoluteFile.toString()
            } else {
                imagePath = absoluteFile.toString()
            }
        }

    fun createBitMap(uri: Uri, type: Int): Bitmap {
        val baos = ByteArrayOutputStream()
        return when (type) {
            PROFILE_PIC -> {
                bitmapDataProfilePic = MediaStore.Images.Media.getBitmap(
                    BaseApplication.context?.contentResolver, uri
                ) as Bitmap
                bitmapDataProfilePic?.compress(Bitmap.CompressFormat.JPEG, 30, baos)
                bitmapDataProfilePic as Bitmap
            }
            PAN -> {
                bitmapDataPAN = MediaStore.Images.Media.getBitmap(
                    BaseApplication.context?.contentResolver, uri
                ) as Bitmap
                bitmapDataPAN?.compress(Bitmap.CompressFormat.JPEG, 30, baos)
                bitmapDataPAN as Bitmap
            }
            FRONT_AADHAR -> {
                bitmapDataAADHARFRONT = MediaStore.Images.Media.getBitmap(
                    BaseApplication.context?.contentResolver, uri
                ) as Bitmap
                bitmapDataAADHARFRONT?.compress(Bitmap.CompressFormat.JPEG, 30, baos)
                bitmapDataAADHARFRONT as Bitmap
            }
            BACK_AADHAR -> {
                bitmapDataAADHARBACK = MediaStore.Images.Media.getBitmap(
                    BaseApplication.context?.contentResolver, uri
                ) as Bitmap
                bitmapDataAADHARBACK?.compress(Bitmap.CompressFormat.JPEG, 30, baos)
                bitmapDataAADHARBACK as Bitmap
            }
            FRONT_DL -> {
                bitmapDataDLFront = MediaStore.Images.Media.getBitmap(
                    BaseApplication.context?.contentResolver, uri
                ) as Bitmap
                bitmapDataDLFront?.compress(Bitmap.CompressFormat.JPEG, 30, baos)
                bitmapDataDLFront as Bitmap
            }
            BACK_DL -> {
                bitmapDataDLBack = MediaStore.Images.Media.getBitmap(
                    BaseApplication.context?.contentResolver, uri
                ) as Bitmap
                bitmapDataDLBack?.compress(Bitmap.CompressFormat.JPEG, 30, baos)
                bitmapDataDLBack as Bitmap
            }
            FRONT_RC -> {
                bitmapDataRCFront = MediaStore.Images.Media.getBitmap(
                    BaseApplication.context?.contentResolver, uri
                ) as Bitmap
                bitmapDataRCFront?.compress(Bitmap.CompressFormat.JPEG, 30, baos)
                bitmapDataRCFront as Bitmap
            }
            BACK_RC -> {
                bitmapDataRCBack = MediaStore.Images.Media.getBitmap(
                    BaseApplication.context?.contentResolver, uri
                ) as Bitmap
                bitmapDataRCBack?.compress(Bitmap.CompressFormat.JPEG, 30, baos)
                bitmapDataRCBack as Bitmap
            }
            BANK_DOC -> {
                bitmapDataBankDoc = MediaStore.Images.Media.getBitmap(
                    BaseApplication.context?.contentResolver, uri
                ) as Bitmap
                bitmapDataBankDoc?.compress(Bitmap.CompressFormat.JPEG, 30, baos)
                bitmapDataBankDoc as Bitmap
            }
            else -> {
                null as Bitmap
            }
        }
    }

    fun uploadImageFile(
        uploadActivity: Context, token: String?, data: Bitmap, filename: String
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

            var client: OkHttpClient = OkHttpClient.Builder().connectTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES).readTimeout(5, TimeUnit.MINUTES).build()
            val requestBody: RequestBody =
                MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart(
                    "file", f.toString(), RequestBody.create("image/png".toMediaTypeOrNull(), f)
                ).build()
            val request: Request =
                Request.Builder().post(requestBody).url("https://api.mitra.vahan.co/upload")
                    //.url("https://stg-api.mitra.vahan.co/upload")
                    .addHeader("Timestamp", System.currentTimeMillis().toString())
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("Authorization", token!!).addHeader("app-build", "signed").addHeader(
                        "device_id", PrefrenceUtils.retriveData(uploadActivity, Constants.DEVICE_ID)
                    ).build()
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
        context: Context?, source: JsonObject?
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