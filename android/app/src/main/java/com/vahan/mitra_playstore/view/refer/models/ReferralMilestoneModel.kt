package com.vahan.mitra_playstore.view.refer.models


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class ReferralMilestoneModel(
    @SerializedName("latestReferralStageNumber")
    val latestReferralStageNumber: String?,
    @SerializedName("numberOfDaysUntilExpiry")
    val numberOfDaysUntilExpiry: String?,
    @SerializedName("payoutDetails")
    val payoutDetails: PayoutDetails?,
    @SerializedName("referralStages")
    val referralStages: List<ReferralStage?>?,
    @SerializedName("referredPhoneNumber")
    val referredPhoneNumber: String?,
    @SerializedName("totalAmountEarned")
    val totalAmountEarned: String?,
    @SerializedName("totalNumberOfTrips")
    val totalNumberOfTrips: String?,
    @SerializedName("totalReferralStages")
    val totalReferralStages: String?
) {
    @Keep
    data class PayoutDetails(
        @SerializedName("label")
        val label: String?,
        @SerializedName("link")
        val link: String?,
        @SerializedName("isEnabled")
        val isEnabled: Boolean?
    )

    @Keep
    data class ReferralStage(
        @SerializedName("groupKey")
        val groupKey: String?,
        @SerializedName("info")
        val info: String?,
        @SerializedName("key")
        val key: String?,
        @SerializedName("label")
        val label: String?,
        @SerializedName("stage")
        val stage: Int?,
        @SerializedName("stageCompletedAt")
        val stageCompletedAt: String?
    )
}