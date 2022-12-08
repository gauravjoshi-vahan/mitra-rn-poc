package com.vahan.mitra_playstore.view.weeklyearnings.datamodels


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import com.google.gson.annotations.Expose

@Keep
data class DailyOrderModel(
    @SerializedName("heading")
    @Expose
    val heading: String,
    @SerializedName("label")
    @Expose
    val label: String,
    @SerializedName("orderLevelDetails")
    @Expose
    val orderLevelDetails: List<OrderLevelDetail>,
    @SerializedName("orderLevelSummary")
    @Expose
    val orderLevelSummary: OrderLevelSummary
) {
    @Keep
    data class OrderLevelDetail(
        @SerializedName("details")
        @Expose
        val details: List<Detail>,
        @SerializedName("orderHeader")
        @Expose
        val orderHeader: OrderHeader,
        @SerializedName("status")
        @Expose
        val status: List<Statu>
    ) {
        @Keep
        data class Detail(
            @SerializedName("label")
            @Expose
            val label: String,
            @SerializedName("value")
            @Expose
            val value: String
        )

        @Keep
        data class OrderHeader(
            @SerializedName("distanceCovered")
            @Expose
            val distanceCovered: List<DistanceCovered>,
            @SerializedName("orderId")
            @Expose
            val orderId: List<OrderId>,
            @SerializedName("tripTimeAndAmount")
            @Expose
            val tripTimeAndAmount: List<TripTimeAndAmount>
        ) {
            @Keep
            data class DistanceCovered(
                @SerializedName("label")
                @Expose
                val label: String,
                @SerializedName("value")
                @Expose
                val value: String
            )

            @Keep
            data class OrderId(
                @SerializedName("label")
                @Expose
                val label: String,
                @SerializedName("value")
                @Expose
                val value: String
            )

            @Keep
            data class TripTimeAndAmount(
                @SerializedName("label")
                @Expose
                val label: String,
                @SerializedName("value")
                @Expose
                val value: String
            )
        }

        @Keep
        data class Statu(
            @SerializedName("label")
            @Expose
            val label: String,
            @SerializedName("value")
            @Expose
            val value: String
        )
    }

    @Keep
    data class OrderLevelSummary(
        @SerializedName("summaryBreakUp")
        @Expose
        val summaryBreakUp: List<SummaryBreakUp>,
        @SerializedName("totalOrderSummary")
        @Expose
        val totalOrderSummary: List<TotalOrderSummary>
    ) {
        @Keep
        data class SummaryBreakUp(
            @SerializedName("label")
            @Expose
            val label: String,
            @SerializedName("value")
            @Expose
            val value: String
        )

        @Keep
        data class TotalOrderSummary(
            @SerializedName("label")
            @Expose
            val label: String,
            @SerializedName("value")
            @Expose
            val value: String
        )
    }
}