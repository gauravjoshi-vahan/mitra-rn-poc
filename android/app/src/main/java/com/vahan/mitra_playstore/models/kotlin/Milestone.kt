package com.vahan.mitra_playstore.models.kotlin


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
@Keep
data class Milestone(
    @SerializedName("companyIcon")
    val companyIcon: String?,
    @SerializedName("companyName")
    val companyName: String?,
    @SerializedName("earningsTitle")
    val earningsTitle: String?,
    @SerializedName("earningsValue")
    val earningsValue: Int?,
    @SerializedName("payoutStructure")
    val payoutStructure: PayoutStructure?,
    @SerializedName("viewDetails")
    val viewDetails: Boolean?
)