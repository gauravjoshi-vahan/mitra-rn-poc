package com.vahan.mitra_playstore.models.kotlin


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class ItemX(
    @SerializedName("extraEarningsLabel")
    val extraEarningsLabel: String?,
    @SerializedName("key")
    val key: String?,
    @SerializedName("label")
    val label: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("unit")
    val unit: String?,
    @SerializedName("unitValue")
    val unitValue: Double?,
    @SerializedName("value")
    val value: Int?
)