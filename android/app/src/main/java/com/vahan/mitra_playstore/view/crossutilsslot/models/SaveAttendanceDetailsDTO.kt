package com.vahan.mitra_playstore.view.crossutilsslot.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class SaveAttendanceDetailsDTO(
    @Json(name = "message")
    val message: String?,
    @Json(name = "success")
    val success: Boolean?
)