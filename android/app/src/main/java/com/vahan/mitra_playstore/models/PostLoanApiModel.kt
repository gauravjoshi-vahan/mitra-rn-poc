package com.vahan.mitra_playstore.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

data class PostLoanApiModel(
    @SerializedName("statusCode")
    @Expose
    var statusCode: Int? = null,
    @SerializedName("emi")
    @Expose
    val emi: Double? = null,
    @SerializedName("duration")
    @Expose
    val duration: Double? = null,
    @SerializedName("periodKey")
    @Expose
    val periodKey: String? = null,
    @SerializedName("periodValue")
    @Expose
    val periodValue: String? = null,
    @SerializedName("emiPeriodKey")
    @Expose
    val emiPeriodKey: String? = null,
    @SerializedName("emiPeriodValue")
    @Expose
    val emiPeriodValue: String? = null,
    @SerializedName("weeklyDebit")
    @Expose
    val weeklyDebit: Double? = null
)
