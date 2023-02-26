package com.vahan.mitra_playstore.repository

import com.vahan.mitra_playstore.di.BaseApiService
import com.vahan.mitra_playstore.di.DeviceApiService
import com.vahan.mitra_playstore.models.kotlin.*
import com.vahan.mitra_playstore.network.kotlin.ApiNetwork
import com.vahan.mitra_playstore.utils.toResultFlow
import com.vahan.mitra_playstore.view.crossutilsslot.models.ReqModelSaveAttendance
import com.vahan.mitra_playstore.view.experiments.mitrapaylater.models.ReqModelMPLCredit
import com.vahan.mitra_playstore.view.experiments.savingcalculator.data.ApplySavingRequestModel
import com.vahan.mitra_playstore.view.experiments.savingcalculator.data.WithDrawRequestModel
import com.vahan.mitra_playstore.view.jobsmarketplace.docupload.datamodels.JMDocInputFieldPostDTO
import com.vahan.mitra_playstore.view.refer.models.ReferralCodeReqModel
import com.vahan.mitra_playstore.view.refer.models.ReferralHomeRequestModel
import com.vahan.mitra_playstore.view.refer.models.ReferralInviteRequestModel
import com.vahan.mitra_playstore.view.refer.models.ReferralMilestoneRequestModel
import javax.inject.Inject

class VahaanRepository @Inject constructor(
    @BaseApiService private val apiNetwork: ApiNetwork,
    @DeviceApiService private val deviceNetwork: ApiNetwork
) {
    fun sentOtp(otp: SendOtp) = toResultFlow {
        deviceNetwork.sendOtp(otp)
    }

    fun login(loginRequest: LoginRequest) = toResultFlow {
        deviceNetwork.login(loginRequest)
    }

    fun verifyAdaharCard(adaharCard: VerifyAdaharCard) = toResultFlow {
        apiNetwork.verifyAadharCard(adaharCard)
    }

    fun getEarnList() = toResultFlow {
        apiNetwork.getEarnList()
    }

    fun getDynamicEntryContentVideoList() = toResultFlow {
        apiNetwork.getDynamicEntryContentVideoList()
    }

    fun createAccount(account: Account) = toResultFlow {
        apiNetwork.createAccount(account)
    }

    fun getBankInfo() = toResultFlow {
        apiNetwork.getBankInfo()
    }

    fun getIFSCDetails(ifsc: String) = toResultFlow {
        apiNetwork.getIFSCDetails(ifsc)
    }

    fun getBreakupInfo(id: String) = toResultFlow {
        apiNetwork.getBreakupInfo(id)
    }

    fun getInsuranceDetails() = toResultFlow {
        apiNetwork.getInsuranceDetails()
    }

    fun getTransactionDetails(
        transaction: Transaction
    ) = toResultFlow {
        apiNetwork.getTransactionDetailsEarn(transaction)
    }

    fun getTransactionDetailsFilterConstraints() = toResultFlow {
        apiNetwork.getTransactionDetailsFilterConstraints()
    }

    fun getTransactionDetailInfo(id: String) = toResultFlow {
        apiNetwork.getTransactionDetailInfo(id)
    }

    fun paymeMoney(paymeMoney: PaymeMoney) = toResultFlow {
        apiNetwork.paymeMoney(paymeMoney)
    }

    fun getPayslip(
        startPage: String,
        totalPage: String
    ) = toResultFlow {
        apiNetwork.getPayslip(startPage, totalPage)
    }

    fun getPurPosesAndAmount() = toResultFlow {
        apiNetwork.getPurPosesAndAmount()
    }

    fun postLoanResponse(postLoan: PostLoan) = toResultFlow {
        apiNetwork.postLoanResponse(postLoan)
    }

    fun postLoanApiInfo(postLoanSummary: PostLoanSummary) = toResultFlow {
        apiNetwork.postLoanApiInfo(postLoanSummary)
    }

    fun postApplyLoan(postLoanApply: PostLoanApply) = toResultFlow {
        apiNetwork.postApplyLoan(postLoanApply)
    }

    fun getLoanList() = toResultFlow {
        apiNetwork.getLoanList()
    }

    fun getBanner(
        companyName: String,
        type: String,
        city: String
    ) = toResultFlow {
        apiNetwork.getBanner(companyName, type, city)
    }

    fun getWeeklyEarnings(
        startDate: String,
        endDate: String
    ) = toResultFlow {
        apiNetwork.getWeeklyEarnings(startDate, endDate)
    }

    fun getWeeklyPayslips(
        startDate: String,
        endDate: String
    ) = toResultFlow {
        apiNetwork.getWeeklyPayslips(startDate, endDate)
    }

    fun getHomeReferralHomeData(data: ReferralHomeRequestModel) = toResultFlow {
        apiNetwork.getHomeReferralHomeData(data)
    }

    fun sentInviteReferral(data: ReferralInviteRequestModel) = toResultFlow {
        apiNetwork.sentLink(data)
    }

    fun getReferralStatusData(data: ReferralHomeRequestModel) = toResultFlow {
        apiNetwork.getReferralStatusModel(data)
    }

    fun getHomeReferralNewHomeData(data: ReferralHomeRequestModel) = toResultFlow {
        apiNetwork.getHomeReferralNewHomeData(data)
    }

    fun getReferralStatusNewData(data: ReferralHomeRequestModel) = toResultFlow {
        apiNetwork.getReferralStatusNewModel(data)
    }

    fun getReferralMilestoneData(data: ReferralMilestoneRequestModel) = toResultFlow {
        apiNetwork.getReferralMilestoneModel(data)
    }

    fun getDailyOrder(
        date: String,
        company: String
    ) = toResultFlow {
        apiNetwork.getDailyOrders(date, company)
    }

    fun getFetchDetails() = toResultFlow {
        apiNetwork.fetchDetails()
    }

    fun withDrawAmount(data: WithDrawRequestModel) = toResultFlow {
        apiNetwork.withDrawAmount(data)
    }

    fun applySaving(data: ApplySavingRequestModel) = toResultFlow {
        apiNetwork.applySaving(data)
    }

    fun getReferralCode(referralCode: ReferralCodeReqModel) = toResultFlow {
        apiNetwork.getReferralCode(referralCode)
    }

    fun validateOtp(number: ValidateReferralRequestModel) = toResultFlow {
        apiNetwork.validateReferralCode(number)
    }

    fun getFetchAttendance() = toResultFlow {
        apiNetwork.getFetchAttendance()
    }

    fun saveAttendanceForUser(reqModel: ReqModelSaveAttendance) = toResultFlow {
        apiNetwork.saveAttendanceForUser(reqModel)
    }

    fun showSaveGoal(reqParm: String) = toResultFlow {
        apiNetwork.showSaveGoal(reqParm)
    }

    fun getWeeklyHistory() = toResultFlow {
        apiNetwork.getWeeklyHistory()
    }

    fun getNudges() = toResultFlow {
        apiNetwork.getNudges()
    }

    fun getJMUploadDocuments(
        siId: String
    ) = toResultFlow {
        apiNetwork.getJMUploadDocs(siId)
    }

    fun getAllRateCardDetails() = toResultFlow {
        apiNetwork.getAllRateCardDetails()
    }

    fun mplCredit(amount: ReqModelMPLCredit) = toResultFlow {
        apiNetwork.mplCredit(amount)
    }

    fun postDocInpField(data: JMDocInputFieldPostDTO) = toResultFlow {
        apiNetwork.postDocInpField(data)
    }


}