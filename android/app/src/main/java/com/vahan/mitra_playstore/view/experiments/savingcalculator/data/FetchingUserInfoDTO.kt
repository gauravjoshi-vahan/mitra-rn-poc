package com.vahan.mitra_playstore.view.experiments.savingcalculator.data


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

@Keep
@JsonClass(generateAdapter = true)
data class FetchingUserInfoDTO(
    @Json(name = "isSavingsPlanPresent")
    val isSavingsPlanPresent: Boolean?,
    @Json(name = "message")
    val message: String?,
    @Json(name = "savingsPlanDetails")
    val savingsPlanDetails: SavingsPlanDetails?,
    @Json(name = "savingsTransactions")
    val savingsTransactions: List<SavingsTransaction?>?,
    @Json(name = "savingsWalletBalances")
    val savingsWalletBalances: SavingsWalletBalances?,
    @Json(name = "futureInstallmentDetails")
    val futureInstallmentDetails: FutureInstalmentDate?,
    @Json(name = "success")
    val success: Boolean?,
    @Json(name = "user")
    val user: User?
) {
    @Keep
    @JsonClass(generateAdapter = true)
    data class SavingsPlanDetails(
        @Json(name = "amount")
        val amount: String?,
        @Json(name = "amountTransferredToMitraWallet")
        val amountTransferredToMitraWallet: Boolean?,
        @Json(name = "createdAt")
        val createdAt: String?,
        @Json(name = "deletedAt")
        val deletedAt: Any?,
        @Json(name = "endDate")
        val endDate: Any?,
        @Json(name = "frequency")
        val frequency: String?,
        @Json(name = "id")
        val id: String?,
        @Json(name = "remarks")
        val remarks: Any?,
        @Json(name = "startDate")
        val startDate: String?,
        @Json(name = "status")
        val status: String?,
        @Json(name = "updatedAt")
        val updatedAt: String?,
        @Json(name = "userId")
        val userId: String?
    )

    @Keep
    @JsonClass(generateAdapter = true)
    data class SavingsTransaction(
        @Json(name = "amount")
        val amount: String?,
        @Json(name = "createdAt")
        val createdAt: String?,
        @Json(name = "deletedAt")
        val deletedAt: Any?,
        @Json(name = "event")
        val event: String?,
        @Json(name = "id")
        val id: String?,
        @Json(name = "isLatest")
        val isLatest: Boolean?,
        @Json(name = "savingsSystematicInstructionId")
        val savingsSystematicInstructionId: String?,
        @Json(name = "status")
        val status: String?,
        @Json(name = "type")
        val type: String?,
        @Json(name = "updatedAt")
        val updatedAt: String?,
        @Json(name = "valueDate")
        val valueDate: String?
    )

    @Keep
    @JsonClass(generateAdapter = true)
    data class SavingsWalletBalances(
        @Json(name = "accruedAmount")
        val accruedAmount: String?,
        @Json(name = "amount")
        val amount: String?,
        @Json(name = "createdAt")
        val createdAt: String?,
        @Json(name = "deletedAt")
        val deletedAt: Any?,
        @Json(name = "id")
        val id: String?,
        @Json(name = "updatedAt")
        val updatedAt: String?,
        @Json(name = "userId")
        val userId: String?,
        @Json(name = "valueDate")
        val valueDate: String?
    )

    @Keep
    @JsonClass(generateAdapter = true)
    data class User(
        @Json(name = "id")
        val id: String?
    )

    @Keep
    data class FutureInstalmentDate(
        @Json(name = "installmentDayName")
        val installmentDayName: String?,
        @Json(name = "futureInstallmentDate")
        val futureInstallmentDate:String
    )
}