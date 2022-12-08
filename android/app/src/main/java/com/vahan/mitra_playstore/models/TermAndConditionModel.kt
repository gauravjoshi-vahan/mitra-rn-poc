package com.vahan.mitra_playstore.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class TermAndConditionModel(
    @SerializedName("tc_url")
    @Expose
    val tcUrl: String? = null,

    @SerializedName("privacy_url")
    @Expose
     val privacyUrl: String? = null
)