package com.vahan.mitra_playstore.view.earn.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class NudgesModel(
    @SerializedName("statusCode")
    var statusCode: Int?,
    @SerializedName("status")
    var status: String?,
    @SerializedName("nudgeDetails")
    val nudgeDetails: NudgeDetails?
) {
    @Keep
    data class NudgeDetails(
        @SerializedName("bgColor")
        val bgColor: String?,
        @SerializedName("ctaLink")
        val ctaLink: String?,
        @SerializedName("ctaText")
        val ctaText: String?,
        @SerializedName("ctaTextColor")
        val ctaTextColor: String?,
        @SerializedName("icon")
        val icon: String?,
        @SerializedName("linkType")
        val linkType: String?,
        @SerializedName("text")
        val text: String?,
        @SerializedName("textColor")
        val textColor: String?
    )
}