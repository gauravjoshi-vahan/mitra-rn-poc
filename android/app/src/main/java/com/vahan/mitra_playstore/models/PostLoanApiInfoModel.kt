package com.vahan.mitra_playstore.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


data class PostLoanApiInfoModel(
    @SerializedName("statusCode")
    @Expose
    var statusCode: Int? = null,
    @SerializedName("name")
    @Expose
    val name: String? = null,
    @SerializedName("emi")
    @Expose
    val emi: Double? = null,
    @SerializedName("amount")
    @Expose
    val amount: Double? = null,
    @SerializedName("duration")
    @Expose
    val duration: Double? = null,
    @SerializedName("loanProviderName")
    @Expose
    val loanProviderName: String? = null,
    @SerializedName("loanProviderLogo")
    @Expose
    val loanProviderLogo: String? = null,

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