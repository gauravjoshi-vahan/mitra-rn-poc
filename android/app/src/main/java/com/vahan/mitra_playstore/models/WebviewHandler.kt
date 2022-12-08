package com.vahan.mitra_playstore.models

data class WebviewHandler(
    val webview_app_handler: List<WebViewDataHandler>? = null
) {
    inner class WebViewDataHandler {
        var url: String? = null
        var destination: String? = null
    }
}