package com.vahan.mitra_playstore.view.refer.models


import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
@Keep
data class ReferralHomeRespModel(
    @Json(name = "earnableAmountForReferrals")
    val earnableAmountForReferrals: List<EarnableAmountForReferral?>?,
    @Json(name = "friendsCompletedMinTrips")
    val friendsCompletedMinTrips: Int?,
    @Json(name = "minTripsForSuccessfulReferral")
    val minTripsForSuccessfulReferral: Int?,
    @Json(name = "referralStagesAndStatusMapping")
    val referralStagesAndStatusMapping: ReferralStagesAndStatusMapping?,
    @Json(name = "referralStatus")
    val referralStatus: List<ReferralStatusModel.ReferralStatus?>?,
    @Json(name = "referralsInProgress")
    val referralsInProgress: Int?,
    @Json(name = "totalAmountEarnedForReferrals")
    val totalAmountEarnedForReferrals: Int?,
    @Json(name = "earnableAmountPerSuccessfulReferral")
    val earnableAmountPerSuccessfulReferral: Int?,
    @Json(name = "totalEarnableAmountPerSuccessfulReferral")
    val totalEarnableAmountPerSuccessfulReferral: Int?,
    @Json(name = "referralValidForDays")
    val referralValidForDays: Int?
) {
    @JsonClass(generateAdapter = true)
    @Keep
    data class EarnableAmountForReferral(
        @Json(name = "earnableAmount")
        val earnableAmount: Int?,
        @Json(name = "numberOfReferrals")
        val numberOfReferrals: Int?
    )

    @JsonClass(generateAdapter = true)
    @Keep
    data class ReferralStagesAndStatusMapping(
        @Json(name = "referralStages")
        val referralStages: List<ReferralStage?>?,
        @Json(name = "totalReferralStages")
        val totalReferralStages: String?
    ) {
        @JsonClass(generateAdapter = true)
        @Keep
        data class ReferralStage(
            @Json(name = "info")
            val info: String?,
            @Json(name = "label")
            val label: String?,
            @Json(name = "stage")
            val stage: String?
        )
    }

    @JsonClass(generateAdapter = true)
    @Keep
    data class ReferralStatus(
        @Json(name = "latestReferralStage")
        val latestReferralStage: String?,
        @Json(name = "name")
        val name: String?,
        @Json(name = "referredPhoneNumber")
        val referredPhoneNumber: String?,
        @Json(name = "isEnabled")
        var isEnabled: Boolean = true
    )
}