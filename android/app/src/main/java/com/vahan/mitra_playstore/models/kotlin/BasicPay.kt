package com.vahan.mitra_playstore.models.kotlin


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class BasicPay(
    @SerializedName("items")
    val items: List<Item>?,
    @SerializedName("label")
    val label: String?,
    @SerializedName("title")
    val title: String?
)