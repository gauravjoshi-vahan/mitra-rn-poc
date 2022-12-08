package com.vahan.mitra_playstore.models


import com.google.gson.annotations.SerializedName

data class DynamicBanner(
    @SerializedName("bannerName")
    val bannerName: Any?,
    @SerializedName("company")
    val company: String?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("deletedAt")
    val deletedAt: Any?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("imageUrl")
    val imageUrl: String?,
    @SerializedName("landingUrl")
    val landingUrl: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("balance")
    val balance: String?,
    @SerializedName("companyLogo")
    val companyLogo: String?
)