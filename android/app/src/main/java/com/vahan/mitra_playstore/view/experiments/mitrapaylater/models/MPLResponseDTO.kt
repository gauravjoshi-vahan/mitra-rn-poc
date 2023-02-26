package com.vahan.mitra_playstore.view.experiments.mitrapaylater.models


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep
data class MPLResponseDTO(
    @SerializedName("message")
    val message: String?,
    @SerializedName("success")
    val success: Boolean?
)