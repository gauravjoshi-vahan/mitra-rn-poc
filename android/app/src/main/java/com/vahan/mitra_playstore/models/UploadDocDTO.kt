package com.vahan.mitra_playstore.models


import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class UploadDocDTO(
    @Json(name = "fileURL")
    @Expose
    var fileURL: String?,

    @Json(name = "success")
    @Expose
    var success: Boolean? = null
)