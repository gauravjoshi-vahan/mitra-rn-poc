package com.vahan.mitra_playstore.models.kotlin


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class ItemXX(
    @SerializedName("key")
    val key: String?,
    @SerializedName("maxValue")
    val maxValue: Int?,
    @SerializedName("values")
    val values: List<Value>?
)