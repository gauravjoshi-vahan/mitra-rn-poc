package com.vahan.mitra_playstore.models.kotlin

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.Keep
import androidx.annotation.RequiresApi
import androidx.core.view.ViewParentCompat
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue


@Keep
@Parcelize
data class EarnDataModel(
    @SerializedName("user")
    @Expose
    val user: User?,
    @SerializedName("jobDetails")
    @Expose
    val jobDetails: JobDetails?,
    @SerializedName("walletDetails")
    @Expose
    val walletDetails: WalletDetails?,
    @SerializedName("currentEarnings")
    @Expose
    val currentEarnings: CurrentEarnings?,
    @SerializedName("documents")
    @Expose
    val documents: Documents?,
    @SerializedName("bankAccountDetails")
    @Expose
    val bankAccountDetails: BankAccountDetails?,
    @SerializedName("cashoutDetails")
    @Expose
    val cashoutDetails: CashOutDetails?, // ui update
    @SerializedName("milestones")
    @Expose
    val milestones: List<Milestone>,
    @SerializedName("crossUtilSlots")
    @Expose
    val crossUtilSlots: List<CrossUtilSlots>?,
    @SerializedName("cashoutAdditionalData")
    @Expose
    val cashoutAdditionalData: CashoutAdditionalData?,  // experiment
    @SerializedName("incentiveStructures")
    @Expose
    val incentiveStructures: @RawValue IncentiveStructures?,
    @SerializedName("attendanceDetails")
    @Expose
    val attendanceDetails: @RawValue AttendanceDetails?,
):Parcelable {
    @Parcelize
    data class User(
        @SerializedName("id")
        @Expose
        val id: String?,
        @SerializedName("jfUserId")
        @Expose
        val jfUserId: String?,
        @SerializedName("name")
        @Expose
        val name: String?,
        @SerializedName("city")
        @Expose
        val city: String?,
        @SerializedName("type")
        @Expose
        val type: String?,
        @SerializedName("email")
        @Expose
        val email: String?,
        @SerializedName("phoneNumber")
        @Expose
        val phoneNumber: String?,
        @SerializedName("settings")
        @Expose
        val settings: @RawValue Settings?,
        @SerializedName("insuranceEligibilityStatus")
        @Expose
        val insuranceEligibilityStatus: String?,
        @SerializedName("cashoutEligibilityStatus")
        @Expose
        val cashoutEligibilityStatus: String?,
        @SerializedName("loanEligibilityStatus")
        @Expose
        val loanEligibilityStatus: String?,
    ): Parcelable

    @Parcelize
    data class CrossUtilSlots(
        @SerializedName("companyIcon")
        @Expose
        val companyIcon: String?,
        @SerializedName("companyKey")
        @Expose
        val companyKey: String?,
        @SerializedName("companyLogo")
        @Expose
        val companyLogo: String?,
        @SerializedName("companyName")
        @Expose
        val companyName: String?,
        @SerializedName("deepLink")
        @Expose
        val deepLink: String?,
        @SerializedName("svgIcon")
        @Expose
        val svgIcon: String?,
        @SerializedName("slots")
        @Expose
        val slots: List<String>?
    ): Parcelable

    @Parcelize
    data class JobDetails(
        @SerializedName("companyEmployeeId")
        @Expose
        val companyEmployeeId: String?,
        @SerializedName("dateOfJoiningStr")
        @Expose
        val dateOfJoiningStr: String?,
        @SerializedName("dateOfJoining")
        @Expose
        val dateOfJoining: String?,
        @SerializedName("companyId")
        @Expose
        val companyId: String?,
        @SerializedName("companyName")
        @Expose
        val companyName: String?,
        @SerializedName("companyKey")
        @Expose
        val companyKey: String?,
        @SerializedName("companyLogo")
        @Expose
        val companyLogo: String?,
        @SerializedName("companyIcon")
        @Expose
        val companyIcon: String?,
        @SerializedName("payrollUserCompanyId")
        @Expose
        val payrollUserCompanyId: String?,
        @SerializedName("nextTransferLabel")
        @Expose
        val nextTransferLabel: String?,
    ): Parcelable

    @Parcelize
    data class Settings(
        @SerializedName("isTestUser")
        @Expose
        val isTestUser: Boolean?,
    ): Parcelable

    @Parcelize
    data class WalletDetails(
        @SerializedName("userId")
        @Expose
        val userId: String?,
        @SerializedName("walletBalance")
        @Expose
        val walletBalance: String?,
        @SerializedName("walletBalanceLabel")
        @Expose
        val walletBalanceLabel: String?,
    ): Parcelable

    @Parcelize
    data class CurrentEarnings(
        @SerializedName("title")
        @Expose
        val title: String?,
        @SerializedName("dateRange")
        @Expose
        val dateRange: String?,
        @SerializedName("amount")
        @Expose
        val amount: String?,
        @SerializedName("id")
        @Expose
        val id: String?,
        @SerializedName("detailedViewAvailable")
        @Expose
        val detailedViewAvailable: Boolean?,
    ): Parcelable

    @Parcelize
    data class Documents(
        @SerializedName("uploaded")
        @Expose
        val uploaded: Boolean?,
        @SerializedName("error")
        @Expose
        val error: String?,
        @SerializedName("documents")
        @Expose
        val documents: List<Documentss>,
    ): Parcelable

    @Parcelize
    data class Documentss(
        @SerializedName("type")
        @Expose
        val type: String?,
        @SerializedName("imageUrl")
        @Expose
        val imageUrl: String?,
        @SerializedName("otherSideImageUrl")
        @Expose
        val otherSideImageUrl: String?,
        @SerializedName("idNumber")
        @Expose
        val idNumber: String?,
        @SerializedName("createdAt")
        @Expose
        val createdAt: String?,
    ): Parcelable

    @Parcelize
    data class BankAccountDetails(
        @SerializedName("available")
        @Expose
        val available: Boolean?,
        @SerializedName("error")
        @Expose
        val error: String?,
        @SerializedName("isBankDetailsEditAvailable")
        @Expose
        val isBankDetailsEditAvailable: Boolean?,
    ): Parcelable

    @Parcelize
    data class CashOutDetails(
        @SerializedName("enabled")
        @Expose
        val enabled: Boolean?,
        @SerializedName("amountEligible")
        @Expose
        val amountEligible: Double?,
        @SerializedName("amountEligibleLabel")
        @Expose
        val amountEligibleLabel: String?,
        @SerializedName("minAmountEligible")
        @Expose
        val minAmountEligible: Int?,
        @SerializedName("isCashoutFeeEnabled")
        @Expose
        val isCashoutFeeEnabled: Boolean?,
        @SerializedName("cashoutFeePercentage")
        @Expose
        val cashoutFeePercentage: Double?,
        @SerializedName("cashoutFixedFee")
        @Expose
        val cashoutFixedFee: Int?,
        @SerializedName("cashoutFixedFeeLabel")
        @Expose
        val cashoutFixedFeeLabel: String?,
        @SerializedName("holdDetails")
        @Expose
        val holdDetails: HoldDetails?,

    ): Parcelable

    @Parcelize
    data class  HoldDetails(
        @SerializedName("isHold")
        @Expose
        val isHold: Boolean?,
        @SerializedName("companyIcon")
        @Expose
        val companyIcon: String?,
        @SerializedName("companyName")
        @Expose
        val companyName: String?,
        @SerializedName("holdStatus")
        @Expose
        val holdStatus: String?,
        @SerializedName("holdMessage")
        @Expose
        val holdMessage: String?,
    ): Parcelable

    @Parcelize
    data class Milestone(
        @SerializedName("viewDetails")
        @Expose
        val viewDetails: Boolean?,
        @SerializedName("lastUpdatedTimestamp")
        @Expose
        val lastUpdatedTimestamp: String?,
        @SerializedName("earningsTitle")
        @Expose
        val earningsTitle: String?,
        @SerializedName("earningsValue")
        @Expose
        val earningsValue: String?,
        @SerializedName("companyName")
        @Expose
        val companyName: String?,
        @SerializedName("companyIcon")
        @Expose
        val companyIcon: String?,
        @SerializedName("payoutStructure")
        @Expose
        val payoutStructure: PayoutStructure,
    ): Parcelable

    @Parcelize
    data class PayoutStructure(
        @SerializedName("title")
        @Expose
        val title: String?,
        @SerializedName("basicPay")
        @Expose
        val basicPay: BasicPay?,
        @SerializedName("incentivePay")
        @Expose
        val incentivePay: IncentivePay?,
        @SerializedName("currentEarnings")
        @Expose
        val currentEarnings: CurrentEarningss?,
    ): Parcelable

    @Parcelize
    data class BasicPay(
        @SerializedName("title")
        @Expose
        val title: String?,
        @SerializedName("label")
        @Expose
        val label: String?,
        @SerializedName("items")
        @Expose
        val items: List<Items>,
    ): Parcelable

    @Parcelize
    data class Items(
        @SerializedName("key")
        @Expose
        val key: String?,
        @SerializedName("name")
        @Expose
        val name: String?,
        @SerializedName("label")
        @Expose
        val label: String?,
        @SerializedName("value")
        @Expose
        val value: String?,
        @SerializedName("unit")
        @Expose
        val unit: String?,
        @SerializedName("unitLabel")
        @Expose
        val unitLabel: String?,
    ): Parcelable

    @Parcelize
    data class IncentivePay(
        @SerializedName("title")
        @Expose
        val title: String?,
        @SerializedName("label")
        @Expose
        val label: String?,
        @SerializedName("structure")
        @Expose
        val structure: String?,
        @SerializedName("structureLabel")
        @Expose
        val structureLabel: String?,
        @SerializedName("tierOrderLabel")
        @Expose
        val tierOrderLabel: String?,
        @SerializedName("tierName")
        @Expose
        val tierName: String?,
        @SerializedName("items")
        @Expose
        val items: List<Itemss>,
    ): Parcelable

    @Parcelize
    data class Itemss(
        @SerializedName("key")
        @Expose
        val key: String?,
        @SerializedName("values")
        @Expose
        val values: List<Values>?,
        @SerializedName("maxValue")
        @Expose
        val maxValue: String?,
    ): Parcelable

    @Parcelize
    data class Values(
        @SerializedName("min")
        @Expose
        val min: String?,
        @SerializedName("max")
        @Expose
        val max: String?,
        @SerializedName("value")
        @Expose
        val value: String?,
    ): Parcelable

    @Parcelize
    data class CurrentEarningss(
        @SerializedName("title")
        @Expose
        val title: String?,
        @SerializedName("items")
        @Expose
        val items: List<Itemsss>,
    ): Parcelable

    @Parcelize
    data class Itemsss(
        @SerializedName("key")
        @Expose
        val key: String,
        @SerializedName("label")
        @Expose
        val label: String?,
        @SerializedName("title")
        @Expose
        val title: String?,
        @SerializedName("value")
        @Expose
        val value: String?,
        @SerializedName("unit")
        @Expose
        val unit: String?,
        @SerializedName("unitValue")
        @Expose
        val unitValue: String?,
        @SerializedName("extraEarningsLabel")
        @Expose
        val extraEarningsLabel: String?,
    ): Parcelable


    @Parcelize
    data class CashoutAdditionalData(
        @SerializedName("cashoutEligibilityStatus")
        val cashoutEligibilityStatus: String?,
        @SerializedName("CashoutExpUser")
        val CashoutExpUser: Boolean?,
        @SerializedName("cashoutNextLevelPercentage")
        val cashoutNextLevelPercentage: Int?,
        @SerializedName("cashoutlevels")
        val cashoutlevels: @RawValue List<Cashoutlevel?>?,
        @SerializedName("userLevel")
        val userLevel: Int?,
        @SerializedName("cashoutPercentage")
        val cashoutPercentage: Int?,
        @SerializedName("orderRequiredToReachToNextLevel")
        val orderRequiredToReachToNextLevel: Int?,
        @SerializedName("daysRequiredTOReachToNextLevel")
        val daysRequiredTOReachToNextLevel: Int?,
        @SerializedName("orderReachToNextLevel")
        val orderReachToNextLevel: Boolean?,
        @SerializedName("daysReachToNextLevel")
        val daysReachToNextLevel: Boolean?,
    ):Parcelable {
        @Parcelize
        data class Cashoutlevel(
            @SerializedName("cashoutPercentage")
            val cashoutPercentage: Int?,
            @SerializedName("days")
            val days: Int?,
            @SerializedName("level")
            val level: Int?,
            @SerializedName("trips")
            val trips: Int?,
        ): Parcelable
    }

    data class IncentiveStructures(
        @SerializedName("type")
        @Expose
        val type: String?,
        @SerializedName("heading")
        @Expose
        val heading: String?,
        @SerializedName("label")
        @Expose
        val label: String?,
        @SerializedName("orderPayTitle")
        @Expose
        val orderPayTitle: String?,
        @SerializedName("orderPayLabel")
        @Expose
        val orderPayLabel: String?,
        @SerializedName("companyLabel")
        @Expose
        val companyLabel: String?,
        @SerializedName("weeklyEarnings")
        val weeklyEarnings: Int?,
        @SerializedName("incentiveDetailsList")
        val incentiveList: List<IncentiveList>,
    ) {
        data class IncentiveList(
            @SerializedName("companyTitle")
            @Expose
            val companyTitle: String?,
            @SerializedName("spinnerKey")
            @Expose
            val spinnerKey: String?,
            @SerializedName("payoutStructureType")
            @Expose
            val payoutStructureType: String?,
            @SerializedName("companies")
            @Expose
            val companies: List<Company?>?,
            @SerializedName("lastUpdatedTimeStamp")
            @Expose
            val lastUpdatedTimeStamp: String?,
            @SerializedName("milestones")
            @Expose
            val milestones: List<Milestone>,
            @SerializedName("messageLabel")
            @Expose
            val messageLabel: String?,
            @SerializedName("payoutStructure")
            @Expose
            val payoutStructure: List<PayoutStructure?>?,
            @SerializedName("milestoneFooter")
            @Expose
            val milestoneFooter: MilestoneFooter?,
        ) {
            data class Company(
                @SerializedName("companyIcon")
                val companyIcon: String?,
                @SerializedName("companyKey")
                val companyKey: String?,
                @SerializedName("companyName")
                val companyName: String?,
                @SerializedName("targetAchieved")
                val targetAchieved: Int?,
                @SerializedName("unit")
                val unit: String?,
                @SerializedName("messageLabel")
                val messageLabel: String?,
            )
            data class MilestoneFooter(
                @SerializedName("label")
                val label: String?,
                @SerializedName("description")
                val description: String?,
            )
            data class Milestone(
                @SerializedName("achieved")
                val achieved: String?,
                @SerializedName("amount")
                val amount: Int?,
                @SerializedName("companyTargets")
                val companyTargets: List<CompanyTargets>?,
                @SerializedName("milestoneLevel")
                val milestoneLevel: String?,
            ) {
                data class CompanyTargets(
                    @SerializedName("companyName")
                    val companyName: String?,
                    @SerializedName("target")
                    val target: Int?,
                    @SerializedName("unit")
                    val unit: String?,
                )
            }
            data class PayoutStructure(
                @SerializedName("value")
                val value: String?,
                @SerializedName("name")
                val name: String?,
                @SerializedName("key")
                val key: String?,
                @SerializedName("label")
                val label: String?,
                @SerializedName("unit")
                val unit: String?,
                @SerializedName("unitLabel")
                val unitLabel: String?,
            )
        }
    }

    @Keep
    @JsonClass(generateAdapter = true)
    data class AttendanceDetails(
        @Json(name = "data")
        val `data`: Data?,
        @Json(name = "message")
        val message: String?,
        @Json(name = "success")
        val success: Boolean?
    ) {
        @Keep
        @JsonClass(generateAdapter = true)
        data class Data(
            @Json(name = "attendanceDetails")
            val attendanceDetails: AttendanceDetails?,
            @Json(name = "checkInMessageReminderTime")
            val checkInMessageReminderTime: String?,
            @Json(name = "checkInRequired")
            val checkInRequired: Boolean?,
            @Json(name = "checkOutMessageReminderTime")
            val checkOutMessageReminderTime: String?,
            @Json(name = "checkOutRequired")
            val checkOutRequired: Boolean?,
            @Json(name = "isAttendanceApplicable")
            val isAttendanceApplicable: Boolean?
        ) {
            @Keep
            @JsonClass(generateAdapter = true)
            class AttendanceDetails
        }
    }
}
