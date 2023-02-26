package com.vahan.mitra_playstore.view.refer.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReferralHomeNewRespModel(
    @Json(name = "bonusAmountObject")
    val bonusAmountObject: List<BonusAmountObject>?,
    @Json(name = "referralStatus")
    val referralStatus: List<ReferralStatu?>?,
    @Json(name = "referralValidForDays")
    val referralValidForDays: Int?,
    @Json(name = "referralsCompleted")
    val referralsCompleted: Int?,
    @Json(name = "referralsInProgress")
    val referralsInProgress: Int?,
    @Json(name = "totalAmountEarnedForReferrals")
    val totalAmountEarnedForReferrals: Int?,
    @Json(name = "referralMessage")
    val referralMessage: String?,
    @Json(name = "totalEarnableAmountPerSuccessfulReferral")
    val totalEarnableAmountPerSuccessfulReferral: Int?
) {
    @JsonClass(generateAdapter = true)
    data class BonusAmountObject(
        @Json(name = "bonusEarned")
        val bonusEarned: Int?,
        @Json(name = "numberOfTrips")
        val numberOfTrips: Int?,
        @Json(name = "totalEarned")
        val totalEarned: Int?
    )

    @JsonClass(generateAdapter = true)
    data class ReferralStatu(
        @Json(name = "latestReferralStageTitle")
        val latestReferralStageTitle: String?,
        @Json(name = "message")
        val message: String?,
        @Json(name = "name")
        val name: String?,
        @Json(name = "referredPhoneNumber")
        val referredPhoneNumber: String?
    )
}