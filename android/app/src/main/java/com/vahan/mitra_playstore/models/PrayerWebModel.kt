package com.vahan.mitra_playstore.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


data class PrayerWebModel(
    @SerializedName("webview_handler")
    @Expose
   val webviewHandler: List<WebviewHandler>? = null
) {
    data class WebviewHandler(
        @SerializedName("url")
        @Expose
        val url: String? = null,
        @SerializedName("destination")
        @Expose
        val destination: String? = null
    ) {

    }
}

