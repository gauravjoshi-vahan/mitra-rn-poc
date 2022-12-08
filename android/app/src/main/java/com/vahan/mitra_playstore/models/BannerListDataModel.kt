package com.vahan.mitra_playstore.models


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BannerListDataModel(
    @SerializedName("dynamicBanner")
    val dynamicBanner: List<DynamicBanner>?,
    @SerializedName("statusCode")
    @Expose
    var statusCode: Int? = null,
    ){

}