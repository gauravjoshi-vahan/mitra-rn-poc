package com.vahan.mitra_playstore.view.experiments.savingcalculator.data


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class WithDrawRespDTO(
    @Json(name = "message")
    val message: String?,
    @Json(name = "success")
    val success: Boolean?
)