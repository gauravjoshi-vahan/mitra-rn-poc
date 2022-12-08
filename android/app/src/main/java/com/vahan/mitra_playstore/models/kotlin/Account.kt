package com.vahan.mitra_playstore.models.kotlin

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class Account(
    val accountNumber:String?,
    val ifsc:String?,
    val accountName:String?
)
