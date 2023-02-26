package com.vahan.mitra_playstore.view.jobsmarketplace.docupload.datamodels


import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class JMPostDocDTO(
    @Json(name = "documents")
    @Expose
    val documents: List<Document?>?,
    @Json(name = "statusCode")
    @Expose
    public var statusCode: Int = 0
) {
    @Keep
    @JsonClass(generateAdapter = true)
    data class Document(
        @Json(name = "docKey")
        @Expose
        val docKey: String?,
        @Json(name = "message")
        @Expose
        val message: String?,
        @Json(name = "status")
        @Expose
        val status: Boolean?
    )
}