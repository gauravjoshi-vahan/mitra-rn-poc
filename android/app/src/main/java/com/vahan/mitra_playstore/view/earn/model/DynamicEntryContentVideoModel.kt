package com.vahan.mitra_playstore.view.earn.model


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import com.google.gson.annotations.Expose

@Keep
data class DynamicEntryContentVideoModel(
    @SerializedName("url")
    @Expose
    val url: String,
    @SerializedName("videoDetails")
    @Expose
    val videoDetails: List<VideoDetail>
) {
    @Keep
    data class VideoDetail(
        @SerializedName("id")
        @Expose
        val id: String,
        @SerializedName("likeCount")
        @Expose
        val likeCount: String,
        @SerializedName("publishedAt")
        @Expose
        val publishedAt: String,
        @SerializedName("tags")
        @Expose
        val tags: List<String>,
        @SerializedName("thumbnail")
        @Expose
        val thumbnail: String,
        @SerializedName("title")
        @Expose
        val title: String,
        @SerializedName("viewCount")
        @Expose
        val viewCount: String,
        @SerializedName("url")
        @Expose
        val url: String
    )
}