package com.vahan.mitra_playstore.models.kotlin


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import com.google.gson.annotations.Expose

@Keep
data class dummy(
    @SerializedName("bankAccountDetails")
    @Expose
    val bankAccountDetails: BankAccountDetails,
    @SerializedName("cashoutAdditionalData")
    @Expose
    val cashoutAdditionalData: CashoutAdditionalData,
    @SerializedName("cashoutDetails")
    @Expose
    val cashoutDetails: CashoutDetails,
    @SerializedName("currentEarnings")
    @Expose
    val currentEarnings: CurrentEarnings,
    @SerializedName("documents")
    @Expose
    val documents: Documents,
    @SerializedName("incentiveStructures")
    @Expose
    val incentiveStructures: IncentiveStructures,
    @SerializedName("jobDetails")
    @Expose
    val jobDetails: JobDetails,
    @SerializedName("milestones")
    @Expose
    val milestones: List<Milestone>,
    @SerializedName("payouts")
    @Expose
    val payouts: List<Any>,
    @SerializedName("user")
    @Expose
    val user: User,
    @SerializedName("walletDetails")
    @Expose
    val walletDetails: WalletDetails
)
{
    @Keep
    data class BankAccountDetails(
        @SerializedName("available")
        @Expose
        val available: Boolean,
        @SerializedName("error")
        @Expose
        val error: String,
        @SerializedName("isBankDetailsEditAvailable")
        @Expose
        val isBankDetailsEditAvailable: Boolean
    )

    @Keep
    data class CashoutAdditionalData(
        @SerializedName("cashoutEligibilityStatus")
        @Expose
        val cashoutEligibilityStatus: String,
        @SerializedName("CashoutExpUser")
        @Expose
        val cashoutExpUser: Boolean,
        @SerializedName("cashoutNextLevelPercentage")
        @Expose
        val cashoutNextLevelPercentage: Int,
        @SerializedName("cashoutPercentage")
        @Expose
        val cashoutPercentage: Any,
        @SerializedName("cashoutlevels")
        @Expose
        val cashoutlevels: List<Cashoutlevel>,
        @SerializedName("daysReachToNextLevel")
        @Expose
        val daysReachToNextLevel: Boolean,
        @SerializedName("daysRequiredTOReachToNextLevel")
        @Expose
        val daysRequiredTOReachToNextLevel: Int,
        @SerializedName("orderReachToNextLevel")
        @Expose
        val orderReachToNextLevel: Boolean,
        @SerializedName("orderRequiredToReachToNextLevel")
        @Expose
        val orderRequiredToReachToNextLevel: Int,
        @SerializedName("userLevel")
        @Expose
        val userLevel: Int
    ) {
        @Keep
        data class Cashoutlevel(
            @SerializedName("cashoutPercentage")
            @Expose
            val cashoutPercentage: Int,
            @SerializedName("days")
            @Expose
            val days: Int,
            @SerializedName("level")
            @Expose
            val level: Int,
            @SerializedName("trips")
            @Expose
            val trips: Int
        )
    }

    @Keep
    data class CashoutDetails(
        @SerializedName("amountEligible")
        @Expose
        val amountEligible: Int,
        @SerializedName("amountEligibleLabel")
        @Expose
        val amountEligibleLabel: String,
        @SerializedName("cashoutFeePercentage")
        @Expose
        val cashoutFeePercentage: Int,
        @SerializedName("cashoutFixedFee")
        @Expose
        val cashoutFixedFee: Int,
        @SerializedName("cashoutFixedFeeLabel")
        @Expose
        val cashoutFixedFeeLabel: String,
        @SerializedName("enabled")
        @Expose
        val enabled: Boolean,
        @SerializedName("isCashoutFeeEnabled")
        @Expose
        val isCashoutFeeEnabled: Boolean,
        @SerializedName("minAmountEligible")
        @Expose
        val minAmountEligible: Int
    )

    @Keep
    data class CurrentEarnings(
        @SerializedName("amount")
        @Expose
        val amount: String,
        @SerializedName("dateRange")
        @Expose
        val dateRange: String,
        @SerializedName("detailedViewAvailable")
        @Expose
        val detailedViewAvailable: Boolean,
        @SerializedName("id")
        @Expose
        val id: String,
        @SerializedName("title")
        @Expose
        val title: String
    )

    @Keep
    data class Documents(
        @SerializedName("documents")
        @Expose
        val documents: List<Document>,
        @SerializedName("error")
        @Expose
        val error: String,
        @SerializedName("uploaded")
        @Expose
        val uploaded: Boolean
    ) {
        @Keep
        data class Document(
            @SerializedName("createdAt")
            @Expose
            val createdAt: String,
            @SerializedName("idNumber")
            @Expose
            val idNumber: String,
            @SerializedName("imageUrl")
            @Expose
            val imageUrl: String,
            @SerializedName("otherSideImageUrl")
            @Expose
            val otherSideImageUrl: String,
            @SerializedName("type")
            @Expose
            val type: String
        )
    }

    @Keep
    data class IncentiveStructures(
        @SerializedName("companyLabel")
        @Expose
        val companyLabel: String,
        @SerializedName("heading")
        @Expose
        val heading: String,
        @SerializedName("incentiveList")
        @Expose
        val incentiveList: List<Incentive>,
        @SerializedName("label")
        @Expose
        val label: String,
        @SerializedName("orderPayLabel")
        @Expose
        val orderPayLabel: String,
        @SerializedName("orderPayTitle")
        @Expose
        val orderPayTitle: String,
        @SerializedName("type")
        @Expose
        val type: String
    ) {
        @Keep
        data class Incentive(
            @SerializedName("companies")
            @Expose
            val companies: List<Company>,
            @SerializedName("companyTitle")
            @Expose
            val companyTitle: String,
            @SerializedName("lastUpdatedTimeStamp")
            @Expose
            val lastUpdatedTimeStamp: Any,
            @SerializedName("messageLabel")
            @Expose
            val messageLabel: String,
            @SerializedName("milestones")
            @Expose
            val milestones: List<Milestone>,
            @SerializedName("payoutStructure")
            @Expose
            val payoutStructure: List<PayoutStructure>,
            @SerializedName("payoutStructureType")
            @Expose
            val payoutStructureType: String
        ) {
            @Keep
            data class Company(
                @SerializedName("companyIcon")
                @Expose
                val companyIcon: String,
                @SerializedName("companyKey")
                @Expose
                val companyKey: String,
                @SerializedName("companyName")
                @Expose
                val companyName: String,
                @SerializedName("messageLabel")
                @Expose
                val messageLabel: String,
                @SerializedName("totalOrder")
                @Expose
                val totalOrder: Int
            )

            @Keep
            data class Milestone(
                @SerializedName("achieved")
                @Expose
                val achieved: String,
                @SerializedName("amount")
                @Expose
                val amount: Int,
                @SerializedName("companyOrders")
                @Expose
                val companyOrders: List<CompanyOrder>,
                @SerializedName("milestoneLevel")
                @Expose
                val milestoneLevel: String
            ) {
                @Keep
                data class CompanyOrder(
                    @SerializedName("companyName")
                    @Expose
                    val companyName: String,
                    @SerializedName("targetOrders")
                    @Expose
                    val targetOrders: Int
                )
            }

            @Keep
            data class PayoutStructure(
                @SerializedName("key")
                @Expose
                val key: String,
                @SerializedName("label")
                @Expose
                val label: String,
                @SerializedName("name")
                @Expose
                val name: String,
                @SerializedName("unit")
                @Expose
                val unit: String,
                @SerializedName("unitLabel")
                @Expose
                val unitLabel: String,
                @SerializedName("value")
                @Expose
                val value: String
            )
        }
    }

    @Keep
    data class JobDetails(
        @SerializedName("companyEmployeeId")
        @Expose
        val companyEmployeeId: String,
        @SerializedName("companyIcon")
        @Expose
        val companyIcon: String,
        @SerializedName("companyId")
        @Expose
        val companyId: String,
        @SerializedName("companyKey")
        @Expose
        val companyKey: String,
        @SerializedName("companyLogo")
        @Expose
        val companyLogo: String,
        @SerializedName("companyName")
        @Expose
        val companyName: String,
        @SerializedName("dateOfJoining")
        @Expose
        val dateOfJoining: String,
        @SerializedName("dateOfJoiningStr")
        @Expose
        val dateOfJoiningStr: String,
        @SerializedName("nextTransferLabel")
        @Expose
        val nextTransferLabel: String,
        @SerializedName("payrollUserCompanyId")
        @Expose
        val payrollUserCompanyId: String
    )

    @Keep
    data class Milestone(
        @SerializedName("companyIcon")
        @Expose
        val companyIcon: String,
        @SerializedName("companyName")
        @Expose
        val companyName: String,
        @SerializedName("earningsTitle")
        @Expose
        val earningsTitle: String,
        @SerializedName("earningsValue")
        @Expose
        val earningsValue: Int,
        @SerializedName("lastUpdatedTimestamp")
        @Expose
        val lastUpdatedTimestamp: String,
        @SerializedName("payoutStructure")
        @Expose
        val payoutStructure: PayoutStructure,
        @SerializedName("viewDetails")
        @Expose
        val viewDetails: Boolean
    ) {
        @Keep
        data class PayoutStructure(
            @SerializedName("basicPay")
            @Expose
            val basicPay: BasicPay,
            @SerializedName("currentEarnings")
            @Expose
            val currentEarnings: CurrentEarnings,
            @SerializedName("incentivePay")
            @Expose
            val incentivePay: IncentivePay,
            @SerializedName("title")
            @Expose
            val title: String
        ) {
            @Keep
            data class BasicPay(
                @SerializedName("items")
                @Expose
                val items: List<Item>,
                @SerializedName("label")
                @Expose
                val label: String,
                @SerializedName("title")
                @Expose
                val title: String
            ) {
                @Keep
                data class Item(
                    @SerializedName("key")
                    @Expose
                    val key: String,
                    @SerializedName("label")
                    @Expose
                    val label: String,
                    @SerializedName("name")
                    @Expose
                    val name: String,
                    @SerializedName("unit")
                    @Expose
                    val unit: String,
                    @SerializedName("unitLabel")
                    @Expose
                    val unitLabel: String,
                    @SerializedName("value")
                    @Expose
                    val value: Int
                )
            }

            @Keep
            data class CurrentEarnings(
                @SerializedName("items")
                @Expose
                val items: List<Item>,
                @SerializedName("title")
                @Expose
                val title: String
            ) {
                @Keep
                data class Item(
                    @SerializedName("extraEarningsLabel")
                    @Expose
                    val extraEarningsLabel: String,
                    @SerializedName("key")
                    @Expose
                    val key: String,
                    @SerializedName("label")
                    @Expose
                    val label: String,
                    @SerializedName("title")
                    @Expose
                    val title: String,
                    @SerializedName("unit")
                    @Expose
                    val unit: String,
                    @SerializedName("unitValue")
                    @Expose
                    val unitValue: Int,
                    @SerializedName("value")
                    @Expose
                    val value: Int
                )
            }

            @Keep
            data class IncentivePay(
                @SerializedName("items")
                @Expose
                val items: List<Item>,
                @SerializedName("label")
                @Expose
                val label: String,
                @SerializedName("structure")
                @Expose
                val structure: String,
                @SerializedName("structureLabel")
                @Expose
                val structureLabel: String,
                @SerializedName("tierName")
                @Expose
                val tierName: String,
                @SerializedName("tierOrderLabel")
                @Expose
                val tierOrderLabel: String,
                @SerializedName("title")
                @Expose
                val title: String
            ) {
                @Keep
                data class Item(
                    @SerializedName("key")
                    @Expose
                    val key: String,
                    @SerializedName("maxValue")
                    @Expose
                    val maxValue: Int,
                    @SerializedName("values")
                    @Expose
                    val values: List<Value>
                ) {
                    @Keep
                    data class Value(
                        @SerializedName("max")
                        @Expose
                        val max: Int,
                        @SerializedName("min")
                        @Expose
                        val min: Int,
                        @SerializedName("value")
                        @Expose
                        val value: Int
                    )
                }
            }
        }
    }

    @Keep
    data class User(
        @SerializedName("cashoutEligibilityStatus")
        @Expose
        val cashoutEligibilityStatus: String,
        @SerializedName("city")
        @Expose
        val city: String,
        @SerializedName("email")
        @Expose
        val email: String,
        @SerializedName("id")
        @Expose
        val id: String,
        @SerializedName("insuranceEligibilityStatus")
        @Expose
        val insuranceEligibilityStatus: String,
        @SerializedName("jfUserId")
        @Expose
        val jfUserId: String,
        @SerializedName("loanEligibilityStatus")
        @Expose
        val loanEligibilityStatus: String,
        @SerializedName("name")
        @Expose
        val name: String,
        @SerializedName("phoneNumber")
        @Expose
        val phoneNumber: String,
        @SerializedName("settings")
        @Expose
        val settings: Settings,
        @SerializedName("type")
        @Expose
        val type: String
    ) {
        @Keep
        data class Settings(
            @SerializedName("isTestUser")
            @Expose
            val isTestUser: Boolean
        )
    }

    @Keep
    data class WalletDetails(
        @SerializedName("userId")
        @Expose
        val userId: String,
        @SerializedName("walletBalance")
        @Expose
        val walletBalance: Double,
        @SerializedName("walletBalanceLabel")
        @Expose
        val walletBalanceLabel: String
    )
}