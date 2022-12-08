package com.vahan.mitra_playstore.models.kotlin

import androidx.annotation.Keep

@Keep
data class PostLoan(
    val amount:Int?,
    val purpose:String?,
    val interest:Int?
)
