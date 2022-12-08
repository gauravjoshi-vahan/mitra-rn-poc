package com.vahan.mitra_playstore.models


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MilestoneResponseModel(
    @Json(name = "incentiveDetailsList")
    val incentiveDetailsList: List<IncentiveDetails?>?
) {
    @JsonClass(generateAdapter = true)
    data class IncentiveDetails(
        @Json(name = "companies")
        val companies: List<Company?>?,
        @Json(name = "durationType")
        val durationType: String?,
        @Json(name = "label")
        val label: String?,
        @Json(name = "lastUpdatedTimeStamp")
        val lastUpdatedTimeStamp: String?,
        @Json(name = "milestones")
        val milestones: List<Milestone?>?,
        @Json(name = "payoutStructure")
        val payoutStructure: List<PayoutStructure?>?
    ) {
        @JsonClass(generateAdapter = true)
        data class Company(
            @Json(name = "companyIcon")
            val companyIcon: String?,
            @Json(name = "companyKey")
            val companyKey: String?,
            @Json(name = "companyName")
            val companyName: String?,
            @Json(name = "totalOrder")
            val totalOrder: Int?
        )

        @JsonClass(generateAdapter = true)
        data class Milestone(
            @Json(name = "achieved")
            val achieved: Boolean?,
            @Json(name = "amount")
            val amount: Int?,
            @Json(name = "companyKeyVsOrderCountMap")
            val companyKeyVsOrderCountMap: CompanyKeyVsOrderCountMap?,
            @Json(name = "milestoneLevel")
            val milestoneLevel: String?
        ) {
            @JsonClass(generateAdapter = true)
            data class CompanyKeyVsOrderCountMap(
                @Json(name = "rapido")
                val rapido: Int?,
                @Json(name = "sfx")
                val sfx: Int?,
                @Json(name = "uber")
                val uber: Int?
            )
        }

        @JsonClass(generateAdapter = true)
        data class PayoutStructure(
            @Json(name = "amount")
            val amount: Int?,
            @Json(name = "info")
            val info: String?,
            @Json(name = "key")
            val key: String?,
            @Json(name = "label")
            val label: String?,
            @Json(name = "unit")
            val unit: String?
        )
    }
}