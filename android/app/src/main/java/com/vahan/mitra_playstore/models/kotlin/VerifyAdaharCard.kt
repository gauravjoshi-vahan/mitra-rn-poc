package com.vahan.mitra_playstore.models.kotlin

import androidx.annotation.Keep

@Keep
data class VerifyAdaharCard(
    val imageUrl:String?,
    val otherSideImageUrl:String?,
    val expectedType:String?,
    val source:String?
)
