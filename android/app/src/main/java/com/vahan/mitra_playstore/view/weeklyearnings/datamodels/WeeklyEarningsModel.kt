package com.vahan.mitra_playstore.view.weeklyearnings.datamodels

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Keep
@JsonClass(generateAdapter = true)
data class WeeklyEarningsModel(
    @Json(name = "companyDetails")
    @Expose
    val companyDetails: List<CompanyDetail?>?,
    @Json(name = "duration")
    @Expose
    val duration: Duration?,
    @Json(name = "otherEarnings")
    @Expose
    val otherEarnings: OtherEarnings?,
    @Json(name = "pageHeader")
    @Expose
    val pageHeader: String?,
    @Json(name = "pageSubHeader")
    @Expose
    val pageSubHeader: String?,
    @Json(name = "weeklyDayWiseBreakdown")
    @Expose
    val weeklyDayWiseBreakdown: WeeklyDayWiseBreakdown?,
    @Json(name = "weeklyIncentives")
    @Expose
    val weeklyIncentives: List<WeeklyIncentive?>?,
    @Json(name = "weeklyDeduction")
    @Expose
    val weeklyDeduction: Deduction?,
    @Json(name = "netPayout")
    @Expose
    val netPayout: NetPayout?,
) {
    @Keep
    @JsonClass(generateAdapter = true)
    data class CompanyDetail(
        @Json(name = "companyName")
        @Expose
        val companyName: String?,
        @Json(name = "createdAt")
        @Expose
        val createdAt: String?,
        @Json(name = "deletedAt")
        @Expose
        val deletedAt: Any?,
        @Json(name = "displayName")
        @Expose
        val displayName: Any?,
        @Json(name = "icon")
        @Expose
        val icon: String?,
        @Json(name = "id")
        @Expose
        val id: String?,
        @Json(name = "key")
        @Expose
        val key: String?,
        @Json(name = "logo")
        @Expose
        val logo: String?,
        @Json(name = "svgIcon")
        @Expose
        val svgIcon: String?,
        @Json(name = "title")
        @Expose
        val title: String?,
        @Json(name = "updatedAt")
        @Expose
        val updatedAt: String?,
    )

    @Keep
    @JsonClass(generateAdapter = true)
    data class Duration(
        @Json(name = "earningsInRupees")
        @Expose
        val earningsInRupees: Double?,
        @Json(name = "endDate")
        @Expose
        val endDate: String?,
        @Json(name = "startDate")
        @Expose
        val startDate: String?,
    )

    @Keep
    @JsonClass(generateAdapter = true)
    data class WeeklyDayWiseBreakdown(
        @Json(name = "dailyEarnings")
        @Expose
        val dailyEarnings: List<DailyEarning?>?,
    ) {
        @Keep
        @JsonClass(generateAdapter = true)
        data class DailyEarning(
            @Json(name = "amountInRupees")
            @Expose
            val amountInRupees: Double?,
            @Json(name = "dailyBreakdown")
            @Expose
            val dailyBreakdown: List<DailyBreakdown?>?,
            @Json(name = "dayDate")
            @Expose
            val dayDate: String?,
            @Json(name = "startDate")
            @Expose
            val startDate: String?,
        ) {
            @Keep
            @JsonClass(generateAdapter = true)
            data class DailyBreakdown(
                @Json(name = "companyKey")
                @Expose
                val companyKey: String?,
                @Json(name = "viewOrderLabel")
                @Expose
                val viewOrderLabel: String?,
                @Json(name = "isViewOrder")
                @Expose
                val isViewOrder: Boolean?,
                @Json(name = "dailyBreakdownValue")
                @Expose
                val dailyBreakdownValue: List<DailyBreakdownValue?>?,
            ) {
                @Keep
                @JsonClass(generateAdapter = true)
                data class DailyBreakdownValue(
                    @Json(name = "title")
                    @Expose
                    val title: String?,
                    @Json(name = "value")
                    @Expose
                    val value: Double?,
                )
            }
        }
    }

    @Keep
    @JsonClass(generateAdapter = true)
    data class OtherEarnings(
        @Json(name = "otherEarningsBreakdown")
        @Expose
        val otherEarningsBreakdown: List<OtherEarningsBreakdown?>?,
        @Json(name = "totalInRupees")
        @Expose
        val totalInRupees: Double,
    ) {
        @Keep
        data class OtherEarningsBreakdown(
            @Json(name = "amountInRupees")
            @Expose
            val amountInRupees: Double?,
            @Json(name = "companyKey")
            @Expose
            val companyKey: Any?,
            @Json(name = "key")
            @Expose
            val key: String?,
            @Json(name = "title")
            @Expose
            val title: Any?,
        )
    }

    @Keep
    @JsonClass(generateAdapter = true)
    data class WeeklyIncentive(
        @Json(name = "amountInRupees")
        @Expose
        val amountInRupees: Double?,
        @Json(name = "companyKey")
        @Expose
        val companyKey: String?,
        @Json(name = "key")
        @Expose
        val key: String?,
        @Json(name = "title")
        @Expose
        val title: String?,
    )


    @Keep
    @JsonClass(generateAdapter = true)
    data class Deduction(
        @Json(name = "label")
        @Expose
        val label: String?,
        @Json(name = "totalDeduction")
        @Expose
        val totalDeduction: Int?,
        @Json(name = "companyDeduction")
        @Expose
        val companyDeduction: List<CompanyDeduction?>?,
    ) {
        @Keep
        @JsonClass(generateAdapter = true)
        data class CompanyDeduction(
            @Json(name = "companyKey")
            @Expose
            val companyKey: String?,
            @Json(name = "deductionBreakdown")
            @Expose
            val deductionBreakdown: List<DeductionBreakdown?>?,
        ) {
            @Keep
            @JsonClass(generateAdapter = true)
            data class DeductionBreakdown(
                @Json(name = "key")
                @Expose
                val key: String?,
                @Json(name = "amountInRupees")
                @Expose
                val amountInRupees: Int?,
            )
        }
    }

    @Keep
    @JsonClass(generateAdapter = true)
    data class NetPayout(
        @Json(name = "netPayoutLabel")
        @Expose
        val netPayoutLabel: String?,
        @Json(name = "netPayoutValue")
        @Expose
        val netPayoutValue: Int?,
    )
}