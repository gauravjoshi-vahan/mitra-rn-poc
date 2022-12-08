package com.vahan.mitra_playstore.models.kotlin


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CustomDataClass(
    @Json(name = "earnedReferralAmount")
    val earnedReferralAmount: Int?,
    @Json(name = "info")
    val info: String?,
    @Json(name = "label")
    val label: String?,
    @Json(name = "latestReferralStage")
    val latestReferralStage: String?,
    @Json(name = "name")
    val name: String?,
    @Json(name = "referredPhoneNumber")
    val referredPhoneNumber: String?,
    @Json(name = "isEnabled")
    var isEnabled: Boolean = true
)