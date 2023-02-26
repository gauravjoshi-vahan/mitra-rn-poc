package com.vahan.mitra_playstore.view.ratecard.models


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep

@Keep

data class RateCardDetailsDTO(
    @SerializedName("companyLabel")
    val companyLabel: String?,
    @SerializedName("heading")
    val heading: String?,
    @SerializedName("incentiveList")
    val incentiveList: List<Incentive?>?,
    @SerializedName("label")
    val label: String?,
    @SerializedName("orderPayLabel")
    val orderPayLabel: String?,
    @SerializedName("orderPayTitle")
    val orderPayTitle: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("weeklyEarnings")
    val weeklyEarnings: Int?
) {
    @Keep

    data class Incentive(
        @SerializedName("additional")
        val additional: List<Additional?>?,
        @SerializedName("basePay")
        val basePay: List<BasePay?>?,
        @SerializedName("basePayAmount")
        val basePayAmount: Int?,
        @SerializedName("basePayPrefix")
        val basePayPrefix: String?,
        @SerializedName("basePayTitle")
        val basePayTitle: String?,
        @SerializedName("calculatorArray")
        val calculatorArray: List<CalculatorArray?>?,
        @SerializedName("calculatorEnabled")
        val calculatorEnabled: Boolean?,
        @SerializedName("calculatorLabels")
        val calculatorLabels: CalculatorLabels?,
        @SerializedName("companies")
        val companies: List<Company?>?,
        @SerializedName("incentiveMapping")
        val incentiveMapping: List<IncentiveMapping?>?,
        @SerializedName("companyIcon")
        val companyIcon: String?,
        @SerializedName("companyTitle")
        val companyTitle: String?,
        @SerializedName("lastUpdatedTimeStamp")
        val lastUpdatedTimeStamp: String?,
        @SerializedName("messageLabel")
        val messageLabel: String?,
        @SerializedName("milestoneFooter")
        val milestoneFooter: MilestoneFooter?,
        @SerializedName("milestoneIncentives")
        val milestoneIncentives: List<MilestoneIncentive?>?,
        @SerializedName("milestoneIncentivesTitle")
        val milestoneIncentivesTitle: String?,
        @SerializedName("milestones")
        val milestones: List<Milestone?>?,
        @SerializedName("minimumGuarantee")
        val minimumGuarantee: List<MinimumGuarantee?>?,
        @SerializedName("orderPay")
        val orderPay: List<OrderPay?>?,
        @SerializedName("orderPayTitle")
        val orderPayTitle: String?,
        @SerializedName("orderPaylabel")
        val orderPaylabel: String?,
        @SerializedName("payoutStructure")
        val payoutStructure: List<PayoutStructure?>?,
        @SerializedName("payoutStructureType")
        val payoutStructureType: String?,
        @SerializedName("shiftIncentiveAmount")
        val shiftIncentiveAmount: Int?,
        @SerializedName("shiftIncentivePrefix")
        val shiftIncentivePrefix: String?,
        @SerializedName("shiftIncentiveTitle")
        val shiftIncentiveTitle: String?,
        @SerializedName("spinnerKey")
        val spinnerKey: String?,
        @SerializedName("version")
        val version: Int?,
        @SerializedName("weekKey1")
        val weekKey1: String?,
        @SerializedName("weekKey2")
        val weekKey2: String?,
        @SerializedName("weeklyBonus")
        val weeklyBonus: WeeklyBonus?,
        @SerializedName("weeklyImageList")
        val weeklyImageList: List<String?>?,
        @SerializedName("rideOption1")
        val rideOption1: String?,
        @SerializedName("rideOption2")
        val rideOption2: String?,
        @SerializedName("rideOptionsList")
        val rideOptionsList: List<RideOptionList?>?
    ) {

        @Keep
        data class RideOptionList(
            @SerializedName("imageUrl")
            val imageUrl: String?,
            @SerializedName("titleHtml")
            val titleHtml: String?,
            @SerializedName("redirectLink")
            val redirectLink: String?,
            @SerializedName("redirectEnabled")
            val redirectEnabled: Boolean?,
        )


        @Keep
        data class Additional(
            @SerializedName("amount")
            val amount: String?,
            @SerializedName("amountLabel")
            val amountLabel: String?,
            @SerializedName("amountPrefix")
            val amountPrefix: String?,
            @SerializedName("condition")
            val condition: String?,
            @SerializedName("frequency")
            val frequency: String?,
            @SerializedName("label")
            val label: String?,
            @SerializedName("title")
            val title: String?,
            @SerializedName("unit")
            val unit: String?,
            @SerializedName("unitLabel")
            val unitLabel: String?,
            @SerializedName("key")
            val key: String?
        )

        @Keep
        data class BasePay(
            @SerializedName("amount")
            val amount: Int?,
            @SerializedName("amountLabel")
            val amountLabel: String?,
            @SerializedName("frequency")
            val frequency: String?,
            @SerializedName("label")
            val label: String?,
            @SerializedName("title")
            val title: String?,
            @SerializedName("unit")
            val unit: String?,
            @SerializedName("unitLabel")
            val unitLabel: String?
        )

        @Keep
        data class CalculatorArray(
            @SerializedName("earnings")
            val earnings: List<Earnings?>?,
            @SerializedName("orders")
            val orders: String?
        ) {

            @Keep
            data class Earnings(
                @SerializedName("breakup")
                val breakup: String?,
                @SerializedName("perOrder")
                val perOrder: String?,
                @SerializedName("total")
                val total: String?,
                @SerializedName("key")
                val key: String?,
            )
        }

        @Keep

        data class CalculatorLabels(
            @SerializedName("amountLabel")
            val amountLabel: String?,
            @SerializedName("totalShiftUpperLabel")
            val totalShiftUpperLabel: String?,
            @SerializedName("totalShiftBottomLabel")
            val totalShiftBottomLabel: String?,
            @SerializedName("totalUpperLabel")
            val totalUpperLabel: String?,
            @SerializedName("totalBottomLabel")
            val totalBottomLabel: String?,
            @SerializedName("label")
            val label: String?,
            @SerializedName("basePayTitle")
            val basePayTitle: String?,
            @SerializedName("basePayPrefix")
            val basePayPrefix: String?,
            @SerializedName("basePayAmount")
            val basePayAmount: String?,
            @SerializedName("shiftIncentiveAmount")
            val shiftIncentiveAmount: String?,
            @SerializedName("shiftIncentivePrefix")
            val shiftIncentivePrefix: String?,
            @SerializedName("orderLabel")
            val orderLabel: String?,
            @SerializedName("title")
            val title: String?,
            @SerializedName("type")
            val type: String?,
            @SerializedName("shiftIncentiveTitle")
            val shiftIncentiveTitle: String?,
            @SerializedName("perOrderPaymentTitle")
            val perOrderPaymentTitle: String?,
            @SerializedName("weeklyOrderLabel")
            val weeklyOrderLabel: String?
        )


        @Keep
        data class Company(
            @SerializedName("companyIcon")
            val companyIcon: String?,
            @SerializedName("companyKey")
            val companyKey: String?,
            @SerializedName("companyName")
            val companyName: String?,
            @SerializedName("messageLabel")
            val messageLabel: String?,
            @SerializedName("targetAchieved")
            val targetAchieved: Int?,
            @SerializedName("totalOrder")
            val totalOrder: Int?,
            @SerializedName("unit")
            val unit: String?
        )


        @Keep
        data class MilestoneFooter(
            @SerializedName("description")
            val description: String?,
            @SerializedName("label")
            val label: String?
        )

        @Keep

        data class MilestoneIncentive(
            @SerializedName("amount")
            val amount: String?,
            @SerializedName("amountLabel")
            val amountLabel: String?,
            @SerializedName("key")
            val key: String?,
            @SerializedName("label")
            val label: String?,
            @SerializedName("name")
            val name: String?,
            @SerializedName("unit")
            val unit: String?,
            @SerializedName("unitLabel")
            val unitLabel: String?
        )

        @Keep

        data class Milestone(
            @SerializedName("achieved")
            val achieved: String?,
            @SerializedName("amount")
            val amount: Int?,
            @SerializedName("companyOrders")
            val companyOrders: List<CompanyOrder?>?,
            @SerializedName("companyTargets")
            val companyTargets: List<CompanyTarget?>?,
            @SerializedName("milestoneLevel")
            val milestoneLevel: String?
        ) {
            @Keep

            data class CompanyOrder(
                @SerializedName("companyName")
                val companyName: String?,
                @SerializedName("targetOrders")
                val targetOrders: Int?
            )

            @Keep

            data class CompanyTarget(
                @SerializedName("companyName")
                val companyName: String?,
                @SerializedName("target")
                val target: Int?,
                @SerializedName("unit")
                val unit: String?
            )
        }

        @Keep

        data class MinimumGuarantee(
            @SerializedName("amount")
            val amount: Int?,
            @SerializedName("amountLabel")
            val amountLabel: String?,
            @SerializedName("condition")
            val condition: List<Condition?>?,
            @SerializedName("title")
            val title: String?
        ) {
            @Keep

            data class Condition(
                @SerializedName("header")
                val header: String?,
                @SerializedName("value")
                val value: String?
            )
        }

        @Keep
        data class OrderPay(
            @SerializedName("amount")
            val amount: String?,
            @SerializedName("amountLabel")
            val amountLabel: String?,
            @SerializedName("key")
            val key: String?,
            @SerializedName("label")
            val label: String?,
            @SerializedName("name")
            val name: String?,
            @SerializedName("unit")
            val unit: String?,
            @SerializedName("unitLabel")
            val unitLabel: String?
        )

        @Keep

        data class PayoutStructure(
            @SerializedName("key")
            val key: String?,
            @SerializedName("label")
            val label: String?,
            @SerializedName("name")
            val name: String?,
            @SerializedName("unit")
            val unit: String?,
            @SerializedName("unitLabel")
            val unitLabel: String?,
            @SerializedName("value")
            val value: String?
        )

        @Keep

        data class WeeklyBonus(
            @SerializedName("amountHeader")
            val amountHeader: String?,
            @SerializedName("condition")
            val condition: List<Condition?>?,
            @SerializedName("frequency")
            val frequency: String?,
            @SerializedName("label")
            val label: String?,
            @SerializedName("orderHeader")
            val orderHeader: String?,
            @SerializedName("title")
            val title: String?,
            @SerializedName("unit")
            val unit: String?,
            @SerializedName("unitLabel")
            val unitLabel: String?
        ) {
            @Keep
            data class Condition(
                @SerializedName("amount")
                val amount: Int?,
                @SerializedName("amountLabel")
                val amountLabel: String?,
                @SerializedName("amountPrefix")
                val amountPrefix: String?,
                @SerializedName("orderCount")
                val orderCount: Int?,
                @SerializedName("orderCountLabel")
                val orderCountLabel: String?,
                @SerializedName("orderCountSuffix")
                val orderCountSuffix: String?
            )
        }

        @Keep
        data class IncentiveMapping(
            @SerializedName("incentives")
            val incentives: List<String?>?,
            @SerializedName("orderString")
            val orderString: String?
        )
    }
}