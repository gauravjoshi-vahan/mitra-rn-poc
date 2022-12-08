package com.vahan.mitra_playstore.view.crossutilsslot.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class AttendanceDetailsDTO(
    @Json(name = "data")
    val `data`: Data?,
    @Json(name = "message")
    val message: String?,
    @Json(name = "success")
    val success: Boolean?
) {
    @Keep
    @JsonClass(generateAdapter = true)
    data class Data(
        @Json(name = "attendanceDetails")
        val attendanceDetails: AttendanceDetails?,
        @Json(name = "checkInMessageReminderTime")
        val checkInMessageReminderTime: String?,
        @Json(name = "checkInRequired")
        val checkInRequired: Boolean?,
        @Json(name = "checkOutMessageReminderTime")
        val checkOutMessageReminderTime: String?,
        @Json(name = "checkOutRequired")
        val checkOutRequired: Boolean?,
        @Json(name = "isAttendanceApplicable")
        val isAttendanceApplicable: Boolean?
    ) {
        @Keep
        @JsonClass(generateAdapter = true)
        class AttendanceDetails
    }
}