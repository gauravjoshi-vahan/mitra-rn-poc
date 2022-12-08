package com.vahan.mitra_playstore.models


import com.google.gson.annotations.SerializedName

data class ChromeModel(
    @SerializedName("urls")
    val urls: List<Url>?
)