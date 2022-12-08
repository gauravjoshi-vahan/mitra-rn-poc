package com.vahan.mitra_playstore.models.kotlin


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class IncentivePay(
    @SerializedName("items")
    val items: List<ItemXX>?,
    @SerializedName("label")
    val label: String?,
    @SerializedName("structure")
    val structure: String?,
    @SerializedName("structureLabel")
    val structureLabel: String?,
    @SerializedName("tierName")
    val tierName: String?,
    @SerializedName("tierOrderLabel")
    val tierOrderLabel: String?,
    @SerializedName("title")
    val title: String?
)