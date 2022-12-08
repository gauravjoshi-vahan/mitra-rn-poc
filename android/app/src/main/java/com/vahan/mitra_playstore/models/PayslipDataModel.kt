package com.vahan.mitra_playstore.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PayslipDataModel(
    @SerializedName("label")
    @Expose
    val label: String? = null,
    @SerializedName("icon")
    @Expose
    val icon: String? = null,
    @SerializedName("payslips")
    @Expose
    val payslips: List<Payslip>? = null
) {
    class Payslip(
        @SerializedName("dateLabel")
        @Expose
        val dateLabel: String? = null,
        @SerializedName("dateTime")
        @Expose
        val dateTime: String? = null,
        @SerializedName("paySlipUrl")
        @Expose
        val payslipUrl: String? = null
    )
}