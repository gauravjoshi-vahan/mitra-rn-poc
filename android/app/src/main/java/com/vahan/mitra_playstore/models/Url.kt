package com.vahan.mitra_playstore.models


import com.google.gson.annotations.SerializedName

data class Url(
    @SerializedName("chrome_url")
    val chromeUrl: String?
)