package com.vahan.mitra_playstore.view.supporttickets.datamodels


import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class StatusMappingModel(
    @Json(name = "color")
    @Expose
    var color: String?,
    @Json(name = "status")
    @Expose
    var status: Int?,
    @Json(name = "statusText")
    @Expose
    var statusText: String?
)