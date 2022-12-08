package com.vahan.mitra_playstore.view.refer.models


import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class ReferralInviteRspModel(
    @Json(name = "duplicateReferrals")
    val duplicateReferrals: Int?,
    @Json(name = "referralLinks")
    val referralLinks: List<ReferralLink?>?,
    @Json(name = "validReferrals")
    val validReferrals: Int?,
    @Json(name = "validReferralsMessage")
    val validReferralsMessage: String?
) {
    @JsonClass(generateAdapter = true)
    data class ReferralLink(
        @Json(name = "referralLink")
        val referralLink: String?,
        @Json(name = "referralPhoneNumber")
        val referralPhoneNumber: String?
    )
}