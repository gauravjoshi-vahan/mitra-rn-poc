package com.vahan.mitra_playstore.models.kotlin


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class Item(
    @SerializedName("key")
    val key: String?,
    @SerializedName("label")
    val label: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("unit")
    val unit: String?,
    @SerializedName("unitLabel")
    val unitLabel: String?,
    @SerializedName("value")
    val value: Int?
)