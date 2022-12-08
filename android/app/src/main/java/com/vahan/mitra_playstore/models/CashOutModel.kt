package com.vahan.mitra_playstore.model


import com.google.gson.annotations.SerializedName

data class CashOutModel(
    @SerializedName("E")
    val e: E,
    @SerializedName("EC")
    val eC: EC,
    @SerializedName("EW")
    val eW: EW,
    @SerializedName("NE")
    val nE: NE
) {
    data class E(
        @SerializedName("card")
        val card: Int,
        @SerializedName("msg")
        val msg: String,
        @SerializedName("hi_msg")
        val hiMsg: String
    )

    data class EC(
        @SerializedName("card")
        val card: Int,
        @SerializedName("msg")
        val msg: String,
        @SerializedName("hi_msg")
        val hiMsg: String
    )

    data class EW(
        @SerializedName("card")
        val card: Int,
        @SerializedName("msg")
        val msg: String,
        @SerializedName("hi_msg")
        val hiMsg: String
    )

    data class NE(
        @SerializedName("card")
        val card: Int,
        @SerializedName("msg")
        val msg: String,
        @SerializedName("hi_msg")
        val hiMsg: String
    )
}