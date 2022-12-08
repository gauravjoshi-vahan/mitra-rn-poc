package com.vahan.mitra_playstore.models.kotlin


import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
@Keep
data class CurrentEarnings(
    @SerializedName("items")
    @Expose
    val items: List<ItemX>?,
    @SerializedName("title")
    @Expose
    val title: String?
)