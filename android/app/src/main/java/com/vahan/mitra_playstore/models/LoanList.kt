package com.vahan.mitra_playstore.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoanList(
    @SerializedName("statusCode")
    @Expose
    var statusCode: Int? = null,
    @SerializedName("loanApplications")
    @Expose
    val loans: List<LoanApplication>? = null,
    @SerializedName("status")
    @Expose
    val status: String? = null,
    @SerializedName("name")
    @Expose
    val name: String? = null

) {
    data class LoanApplication(
        @SerializedName("amount")
        @Expose
        val amount: String? = null,
        @SerializedName("purpose")
        @Expose
        val purpose: String? = null,

        @SerializedName("duration")
        @Expose
        val duration: Double? = null,

        @SerializedName("interestRate")
        @Expose
        val interestRate: Double? = null,

        @SerializedName("loanProviderName")
        @Expose
        val loanProviderName: String? = null,

        @SerializedName("siplyId")
        @Expose
        val siplyId: String? = null,

        @SerializedName("createdAt")
        @Expose
        val createdAt: String? = null,

        @SerializedName("loanStatus")
        @Expose
        val loanStatus: String? = null,

        @SerializedName("emi")
        @Expose
        val emi: String? = null,
        @SerializedName("emiPeriodKey")
        @Expose
        val emiPeriodKey: String? = null,
        @SerializedName("month_lang")
        @Expose
        val monthLang: String? = null,
        @SerializedName("emiPeriodValue")
        @Expose
        val emiPeriodValue: String? = null,
        @SerializedName("loanProviderLogo")
        @Expose
        val loanProviderLogo: String? = null,
        @SerializedName("weeklyDebit")
        @Expose
        val weeklyDebit: String? = null,
    )
}