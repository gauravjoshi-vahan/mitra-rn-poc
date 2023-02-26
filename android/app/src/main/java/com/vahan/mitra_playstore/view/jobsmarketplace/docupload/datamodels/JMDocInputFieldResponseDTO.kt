package com.vahan.mitra_playstore.view.jobsmarketplace.docupload.datamodels


import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class JMDocInputFieldResponseDTO(
    @Json(name = "candidateDlNumber")
    @Expose
    val candidateDlNumber: String?,
    @Json(name = "panNumber")
    @Expose
    val panNumber: String?
) {
}