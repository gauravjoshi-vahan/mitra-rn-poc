package com.vahan.mitra_playstore.view.refer.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class ReferralCodeRespDTO(
    @Json(name = "code")
    val code: String?,
    @Json(name = "duplicateReferrals")
    val duplicateReferrals: Int?,
    @Json(name = "referralCodeLink")
    val referralCodeLink: String?,
    @Json(name = "referralImageLink")
    val referralImageLink: String?,
    @Json(name = "referralLinks")
    val referralLinks: List<Any?>?,
    @Json(name = "validReferrals")
    val validReferrals: Int?,
    @Json(name = "validReferralsMessage")
    val validReferralsMessage: String?
)