package com.vahan.mitra_playstore.view.refer.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReferralMilestoneModel(
    @Json(name = "latestReferralStageNumber")
    val latestReferralStageNumber: String?,
    @Json(name = "numberOfDaysUntilExpiry")
    val numberOfDaysUntilExpiry: String?,
    @Json(name = "referralStages")
    val referralStages: List<ReferralStage?>?,
    @Json(name = "referredPhoneNumber")
    val referredPhoneNumber: String?,
    @Json(name = "totalAmountEarned")
    val totalAmountEarned: String?,
    @Json(name = "totalNumberOfTrips")
    val totalNumberOfTrips: String?,
    @Json(name = "totalReferralStages")
    val totalReferralStages: String?
) {
    @JsonClass(generateAdapter = true)
    data class ReferralStage(
        @Json(name = "info")
        val info: String?,
        @Json(name = "label")
        val label: String?,
        @Json(name = "stage")
        val stage: String?,
        @Json(name = "stageCompletedAt")
        val stageCompletedAt: String?
    )
}