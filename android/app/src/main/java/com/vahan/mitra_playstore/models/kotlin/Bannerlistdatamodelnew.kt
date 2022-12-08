package com.vahan.mitra_playstore.models.kotlin


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
@Keep
data class BannerListDataModelNew(
    @Json(name = "dynamicBanner")
    val dynamicBanner: List<DynamicBanner?>?,
    val dynamicEntryPoint: List<DynamicEntryPoint?>?
) {
    @JsonClass(generateAdapter = true)
    data class DynamicBanner(
        @Json(name = "bannerName")
        val bannerName: Any?,
        @Json(name = "company")
        val company: String?,
        @Json(name = "createdAt")
        val createdAt: String?,
        @Json(name = "deletedAt")
        val deletedAt: Any?,
        @Json(name = "id")
        val id: String?,
        @Json(name = "imageUrl")
        val imageUrl: String?,
        @Json(name = "landingUrl")
        val landingUrl: String?,
        @Json(name = "rank")
        val rank: Int?,
        @Json(name = "type")
        val type: String?,
        @Json(name = "updatedAt")
        val updatedAt: String?,
        @Json(name = "title")
        val title: String?,
        @Json(name = "balance")
        val balance: String?,
        @Json(name = "companyLogo")
        val companyLogo: String?
    )

    @JsonClass(generateAdapter = true)
    data class DynamicEntryPoint(
        @Json(name = "id")
        val id: String?,
        @Json(name = "bannerName")
        val bannerName: String?,
        @Json(name = "type")
        val type: String?,
        @Json(name = "company")
        val company: String?,
        @Json(name = "imageUrl")
        val imageUrl: String?,
        @Json(name = "landingUrl")
        val landingUrl: String?,
        @Json(name = "createdAt")
        val createdAt: String?,
        @Json(name = "updatedAt")
        val updatedAt: String?,
        @Json(name = "deletedAt")
        val deletedAt: String?,
        @Json(name = "rank")
        val rank: Int?,
        @Json(name = "cityId")
        val cityId: String?,
        @Json(name = "bucket")
        val bucket: Int?,
        @Json(name = "minAvailableUserPartition")
        val minAvailableUserPartition: Int?,
        @Json(name = "maxAvailableUserPartition")
        val maxAvailableUserPartition: Int?,
        @Json(name = "section")
        val section: String?

    )

}