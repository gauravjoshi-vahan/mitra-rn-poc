package com.vahan.mitra_playstore.view.jobsmarketplace.docupload.datamodels


import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class JMDocUploadDTO(
    @Json(name = "additionalInfo")
    @Expose
    val additionalInfo: ArrayList<AdditionalInfo?>?,
    @Json(name = "documents")
    @Expose
    val documents: ArrayList<Document?>?,
    @Json(name = "statusCode")
    @Expose
    public var statusCode: Int = 0,
) {
    @Keep
    @JsonClass(generateAdapter = true)
    data class AdditionalInfo(
        @Json(name = "displayName")
        @Expose
        val displayName: String?,
        @Json(name = "docKey")
        @Expose
        val docKey: String?,
        @Json(name = "key")
        @Expose
        val key: String?,
        @Json(name = "value")
        @Expose
        val value: String?
    )

    @Keep
    @JsonClass(generateAdapter = true)
    data class Document(
        @Json(name = "componentType")
        @Expose
        val componentType: String?,
        @Json(name = "header")
        @Expose
        val header: String?,
        @Json(name = "imageUrl")
        @Expose
        val imageUrl: String?,
        @Json(name = "isCompleted")
        @Expose
        val isCompleted: Boolean?,
        @Json(name = "key")
        @Expose
        var key: String?,
        @Json(name = "otherSideImageUrl")
        @Expose
        val otherSideImageUrl: String?,
        @Json(name = "reason")
        @Expose
        val reason: String?,
        @Json(name = "status")
        @Expose
        val status: String?,
        @Json(name = "subHeader")
        @Expose
        val subHeader: String?,
        @Json(name = "dropdownDocs")
        @Expose
        var dropdownDocs: ArrayList<String>
    )
}