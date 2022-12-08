package com.vahan.mitra_playstore.view.experiments.weeklygoal.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class WeeklyGoalDTO(
    @Json(name = "message")
    val message: String?,
    @Json(name = "success")
    val success: Boolean?,
    @Json(name = "supplyHours")
    val supplyHours: Int?
)