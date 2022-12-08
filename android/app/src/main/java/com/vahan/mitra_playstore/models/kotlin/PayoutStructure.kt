package com.vahan.mitra_playstore.models.kotlin


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class PayoutStructure(
    @SerializedName("basicPay")
    val basicPay: BasicPay?,
    @SerializedName("currentEarnings")
    val currentEarnings: CurrentEarnings?,
    @SerializedName("incentivePay")
    val incentivePay: IncentivePay?,
    @SerializedName("title")
    val title: String?
)