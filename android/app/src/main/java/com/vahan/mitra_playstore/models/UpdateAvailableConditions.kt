package com.vahan.mitra_playstore.models

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class UpdateAvailableConditions {
    @SerializedName("update_available")
    @Expose
    var updateAvailable: Int? = null

    @SerializedName("only_test_user")
    @Expose
    var onlyTestUser: Int? = null

    @SerializedName("version")
    @Expose
    var version: Double? = null

    @SerializedName("force_update")
    @Expose
    var forceUpdate: Int? = null

    @SerializedName("force_update_version")
    @Expose
    var forceUpdateVersion: Double? = null

}