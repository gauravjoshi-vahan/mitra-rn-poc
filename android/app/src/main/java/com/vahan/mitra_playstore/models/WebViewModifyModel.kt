package com.vahan.mitra_playstore.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class WebViewModifyModel(
    @SerializedName("webview_handler_modified")
    @Expose
    val webviewHandler: List<WebviewHandler>? = null
) {
    data class WebviewHandler(
        @SerializedName("url")
        @Expose
        val url: String? = null,
        @SerializedName("destination")
        @Expose
        val destination: String? = null,
        @SerializedName("download_link")
        @Expose
        val downloadLink: String? = null
    )
}
