package com.vahan.mitra_playstore.models.kotlin

import androidx.annotation.Keep

@Keep
data class LoginRequest(
    var phoneNumber:String?,
    var otp:String?
)
