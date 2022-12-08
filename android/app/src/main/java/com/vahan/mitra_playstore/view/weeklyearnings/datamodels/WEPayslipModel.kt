package com.vahan.mitra_playstore.view.weeklyearnings.datamodels


import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class WEPayslipModel(
    @Json(name = "paySlipDateLabel")
    @Expose
    val paySlipDateLabel: String?,
    @Json(name = "paySlipUrl")
    @Expose
    val paySlipUrl: String?
)