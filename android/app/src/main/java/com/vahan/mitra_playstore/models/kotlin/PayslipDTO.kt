package com.vahan.mitra_playstore.models.kotlin


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class PayslipDTO(
    @Json(name = "icon")
    val icon: String?,
    @Json(name = "label")
    val label: String?,
    @Json(name = "payslips")
    val payslips: List<Payslip>
) {
    @Keep
    @JsonClass(generateAdapter = true)
    data class Payslip(
        @Json(name = "dateLabel")
        val dateLabel: String?,
        @Json(name = "dateTime")
        val dateTime: String?,
        @Json(name = "paySlipUrl")
        val paySlipUrl: String?
    )
}