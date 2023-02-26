package com.vahan.mitra_playstore.view.jobsmarketplace.docupload.datamodels


import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class JMDocUploadStatusDTO(
    @Json(name = "status")
    @Expose
    val status: String?,
    @Json(name = "text")
    @Expose
    val text: String?
)