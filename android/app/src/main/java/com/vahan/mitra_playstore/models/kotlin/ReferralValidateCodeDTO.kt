package com.vahan.mitra_playstore.models.kotlin


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class ReferralValidateCodeDTO(
    @Json(name = "success")
    val success: Boolean?
)