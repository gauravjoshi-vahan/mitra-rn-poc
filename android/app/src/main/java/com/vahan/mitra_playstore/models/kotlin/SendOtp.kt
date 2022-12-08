package com.vahan.mitra_playstore.models.kotlin

import androidx.annotation.Keep

@Keep
data class SendOtp(
    var phoneNumber:String?,
    var appHashKey:String?
)
