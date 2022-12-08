package com.vahan.mitra_playstore.models.kotlin


import androidx.annotation.Keep
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
@Keep
data class TransactionDetailModel(
    @Json(name = "transactions")
    val transactions: List<Transaction?>?,
    @Json(name = "userId")
    val userId: String?,
    @Json(name = "walletBalance")
    val walletBalance: Double?,
    @Json(name = "walletBalanceLabel")
    val walletBalanceLabel: String?
) {
    @JsonClass(generateAdapter = true)
    data class Transaction(
        @Json(name = "amountInPaisa")
        val amountInPaisa: Int?,
        @Json(name = "amountLabel")
        val amountLabel: String?,
        @Json(name = "backgroundColor")
        val backgroundColor: String?,
        @Json(name = "color")
        val color: String?,
        @Json(name = "dateTime")
        val dateTime: String?,
        @Json(name = "dateTimeStr")
        val dateTimeStr: String?,
        @Json(name = "extendedDateTimeStr")
        val extendedDateTimeStr: String?,
        @Json(name = "icon")
        val icon: String?,
        @Json(name = "id")
        val id: String?,
        @Json(name = "key")
        val key: String?,
        @Json(name = "label")
        val label: String?,
        @Json(name = "status")
        val status: String?,
        @Json(name = "statusLabel")
        val statusLabel: String?,
        @Json(name = "title")
        val title: String?,
        @Json(name = "type")
        val type: String?,
        @Json(name = "statusColor")
        val statusColor: String?
    )
}