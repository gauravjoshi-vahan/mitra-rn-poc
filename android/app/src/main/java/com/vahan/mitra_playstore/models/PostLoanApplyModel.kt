package com.vahan.mitra_playstore.models

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


data class PostLoanApplyModel(
    @SerializedName("statusCode")
    @Expose
    var statusCode: Int? = null,
    @SerializedName("message")
    @Expose
    var message: String? = null,
    @SerializedName("success")
    @Expose
    val success: Boolean? = null,

    @SerializedName("userId")
    @Expose
    val userId: String? = null
) {

}