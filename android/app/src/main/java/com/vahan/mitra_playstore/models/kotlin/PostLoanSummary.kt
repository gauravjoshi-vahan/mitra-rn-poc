package com.vahan.mitra_playstore.models.kotlin

import androidx.annotation.Keep

@Keep
data class PostLoanSummary(
    val amount:Int?,
    val purpose:String?,
    val duration:Int?,
    val emi:Double?,
    val period:String?,
    val emiPeriod:String?
)
