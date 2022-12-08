package com.vahan.mitra_playstore.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class FreshchatEnableModel {
    @SerializedName("enabled")
    @Expose
    var enabled: Int? = null
    @SerializedName("version")
    @Expose
    var version: Double? = null
}