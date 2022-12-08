package com.vahan.mitra_playstore.view.experiments.earningHistory.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class WeeklyEarningHistoryDTO(
    @Json(name = "earningsHistory")
    val earningsHistory: List<EarningsHistory?>?,
    @Json(name = "message")
    val message: String?,
    @Json(name = "months")
    val months: List<String?>?,
    @Json(name = "success")
    val success: Boolean?
) {
    @Keep
    @JsonClass(generateAdapter = true)
    data class EarningsHistory(
        @Json(name = "data")
        val `data`: List<Data?>?,
        @Json(name = "month")
        val month: String?
    ) {
        @Keep
        @JsonClass(generateAdapter = true)
        data class Data(
            @Json(name = "earnings")
            val earnings: Int?,
            @Json(name = "endDate")
            val endDate: String?,
            @Json(name = "hoursWorked")
            val hoursWorked: String?,
            @Json(name = "startDate")
            val startDate: String?
        )
    }
}