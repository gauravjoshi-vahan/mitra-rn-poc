package com.vahan.mitra_playstore.model

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class TranscInfoModel(

    @SerializedName("backgroundColor")
    @Expose
    val backgroundColor: String? = null,
    @SerializedName("statusCode")
    @Expose
    var statusCode: Int,

    @SerializedName("id")
    @Expose
    val id: String? = null,

    @SerializedName("type")
    @Expose
    val type: String? = null,

    @SerializedName("amountLabel")
    @Expose
    val amountLabel: String? = null,

    @SerializedName("amountInPaisa")
    @Expose
    val amountInPaisa: Int? = null,

    @SerializedName("key")
    @Expose
    val key: String? = null,

    @SerializedName("icon")
    @Expose
    val icon: String? = null,

    @SerializedName("label")
    @Expose
    val label: String? = null,

    @SerializedName("dateTimeStr")
    @Expose
    val dateTimeStr: String? = null,

    @SerializedName("dateTime")
    @Expose
    val dateTime: String? = null,

    @SerializedName("status")
    @Expose
    val status: String? = null,

    @SerializedName("statusLabel")
    @Expose
    val statusLabel: String? = null,

    @SerializedName("statusColor")
    @Expose
    val statusColor: String? = null,

    @SerializedName("utrNumber")
    @Expose
    val utrNumber: String? = null,

    @SerializedName("title")
    @Expose
    val title: String? = null,

    @SerializedName("color")
    @Expose
    val color: String? = null,

    @SerializedName("info")
    @Expose
    val info: Info? = null,

    @SerializedName("transactionMessage")
    @Expose
    val transactionMessage: String? = null,
) {
    class Info(
        @SerializedName("title")
        @Expose
        val title: String? = null,

        @SerializedName("icon")
        @Expose
        val icon: String? = null,

        @SerializedName("label")
        @Expose
        val label: String? = null,

        @SerializedName("value")
        @Expose
        val value: String? = null,

        @SerializedName("desc")
        @Expose
        val desc: String? = null,

        @SerializedName("message")
        @Expose
        val message: String? = null,
    )

}