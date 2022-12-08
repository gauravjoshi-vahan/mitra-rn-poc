package com.vahan.mitra_playstore.view.supporttickets.datamodels


import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class SupportTicketDTO(
    @Json(name = "results")
    @Expose
    val results: ArrayList<Result>?
) {
    @Keep
    @JsonClass(generateAdapter = true)
    data class Result(
        @Json(name = "created_at")
        @Expose
        val created_at: String?,
        @Json(name = "custom_fields")
        @Expose
        val custom_fields: CustomFields?,
        @Json(name = "id")
        @Expose
        val id: Int?,
        @Json(name = "status")
        @Expose
        val status: Int?,
        @Json(name = "subject")
        @Expose
        val subject: String?,
        @Json(name = "type")
        @Expose
        val type: String?,
        @Json(name = "updated_at")
        @Expose
        val updated_at: String?
    ) {
        @Keep
        @JsonClass(generateAdapter = true)
        data class CustomFields(
            @Json(name = "cf_city")
            @Expose
            val cf_city: String?,
            @Json(name = "cf_client_id")
            @Expose
            val cf_client_id: Long?,
            @Json(name = "cf_client_name")
            @Expose
            val cf_client_name: String?,
            @Json(name = "cf_client_type")
            @Expose
            val cf_client_type: String?,
            @Json(name = "cf_contact")
            @Expose
            val cf_contact: String?,
            @Json(name = "cf_payment_issues_test")
            @Expose
            val cf_payment_issues_test: String?
        )
    }
}