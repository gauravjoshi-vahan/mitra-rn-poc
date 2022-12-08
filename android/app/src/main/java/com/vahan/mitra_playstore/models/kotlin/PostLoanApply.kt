package com.vahan.mitra_playstore.models.kotlin

import androidx.annotation.Keep

@Keep
data class PostLoanApply(
    val amount:Int?,
    val purpose:String?,
    val duration:Int?,
    val interest:Int?,
    val period:String?
)
