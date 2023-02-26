package com.vahan.mitra_playstore.network.kotlin

import com.vahan.mitra_playstore.model.TranscInfoModel
import com.vahan.mitra_playstore.model.cashoutClass
import com.vahan.mitra_playstore.models.*
import com.vahan.mitra_playstore.models.SendOtp
import com.vahan.mitra_playstore.models.kotlin.*
import com.vahan.mitra_playstore.utils.Constants
import com.vahan.mitra_playstore.view.crossutilsslot.models.AttendanceDetailsDTO
import com.vahan.mitra_playstore.view.crossutilsslot.models.ReqModelSaveAttendance
import com.vahan.mitra_playstore.view.crossutilsslot.models.SaveAttendanceDetailsDTO
import com.vahan.mitra_playstore.view.earn.model.DynamicEntryContentVideoModel
import com.vahan.mitra_playstore.view.earn.model.NudgesModel
import com.vahan.mitra_playstore.view.experiments.earningHistory.models.WeeklyEarningHistoryDTO
import com.vahan.mitra_playstore.view.experiments.mitrapaylater.models.MPLResponseDTO
import com.vahan.mitra_playstore.view.experiments.mitrapaylater.models.ReqModelMPLCredit
import com.vahan.mitra_playstore.view.experiments.savingcalculator.data.ApplySavingRequestModel
import com.vahan.mitra_playstore.view.experiments.savingcalculator.data.FetchingUserInfoDTO
import com.vahan.mitra_playstore.view.experiments.savingcalculator.data.WithDrawRequestModel
import com.vahan.mitra_playstore.view.experiments.savingcalculator.data.WithDrawRespDTO
import com.vahan.mitra_playstore.view.experiments.weeklygoal.models.WeeklyGoalDTO
import com.vahan.mitra_playstore.view.jobsmarketplace.docupload.datamodels.JMDocInputFieldPostDTO
import com.vahan.mitra_playstore.view.jobsmarketplace.docupload.datamodels.JMDocInputFieldResponseDTO
import com.vahan.mitra_playstore.view.jobsmarketplace.docupload.datamodels.JMDocUploadDTO
import com.vahan.mitra_playstore.view.ratecard.models.RateCardDetailsDTO
import com.vahan.mitra_playstore.view.refer.models.*
import com.vahan.mitra_playstore.view.weeklyearnings.datamodels.DailyOrderModel
import com.vahan.mitra_playstore.view.weeklyearnings.datamodels.WEPayslipModel
import com.vahan.mitra_playstore.view.weeklyearnings.datamodels.WeeklyEarningsModel
import retrofit2.Response
import retrofit2.http.*

interface ApiNetwork {
    @POST("/auth/sendOtp")
    suspend fun sendOtp(@Body otp: com.vahan.mitra_playstore.models.kotlin.SendOtp): Response<SendOtp>

    @POST("/auth/login")
    suspend fun login(@Body otp: LoginRequest): Response<LoginModel>

    @POST("/user-documents/verify")
    suspend fun verifyAadharCard(@Body source: VerifyAdaharCard): Response<VerificationResponseModel>

    @GET("v2/app/profile")
    suspend fun getEarnList(): Response<EarnDataModel>

    @POST("v2/app/bank_account")
    suspend fun createAccount(@Body jsonObject: Account): Response<VerifyModel>

    @GET("/app/bank_account")
    suspend fun getBankInfo(): Response<GetBankDetailsModel>

    @GET("/" + "{" + Constants.IFSCCODEKEY + "}")
    suspend fun getIFSCDetails(
        @Path(Constants.IFSCCODEKEY) ifscNumber: String
    ): Response<CheckCorrectIFSCModel>

    @GET("app/payroll-earnings/" + "{" + Constants.EARNINGID + "}")
    suspend fun getBreakupInfo(@Path(Constants.EARNINGID) id: String): Response<BreakupModel>

    @GET("v2/app/insurance")
    suspend fun getInsuranceDetails(): Response<InsuranceModel>

    @POST("/v2/app/transactions")
    suspend fun getTransactionDetails(@Body jsonObject: Transaction): Response<TransactionDetailsModelJava>

    @POST("/v2/app/transactions")
    suspend fun getTransactionDetailsEarn(@Body jsonObject: Transaction): Response<TransactionDetailModel>

    @GET("/v2/app/transactions/constraints")
    suspend fun getTransactionDetailsFilterConstraints(): Response<TransactionDetailsFilterConstraintsModel>

    @GET("/v2/app/transactions/{transactionId}/info")
    suspend fun getTransactionDetailInfo(@Path("transactionId") value: String): Response<TranscInfoModel>

    @GET("/v2/app/transactions/constraints")
    suspend fun getTransctionDetailsFilterConstraints(): Response<TransactionDetailsFilterConstraintsModel>

    @POST("v2/app/transactions/cashout")
    suspend fun paymeMoney(@Body jsonObject: PaymeMoney): Response<cashoutClass>

    @GET("v2/app/payout_details/{startPage}/{totalPage}")
    suspend fun getPayslip(
        @Path("startPage") startPage: String,
        @Path("totalPage") totalPage: String
    ): Response<PayslipDTO>

    @GET("v2/app/loan/purposesAndAmounts")
    suspend fun getPurPosesAndAmount(): Response<GetAmountAndPurposeDTO>

    @POST("/v2/app/loan/variables")
    suspend fun postLoanResponse(@Body jsonObject: PostLoan): Response<List<PostLoanApiModel>>

    @POST("/v2/app/loan/summary")
    suspend fun postLoanApiInfo(@Body jsonObject: PostLoanSummary): Response<PostLoanApiInfoModel>

    @POST("/v2/app/loan/apply")
    suspend fun postApplyLoan(@Body jsonObject: PostLoanApply): Response<PostLoanApplyModel>

    @GET("/v2/app/loan/list")
    suspend fun getLoanList(): Response<LoanList>

    @GET("/v2/app/filterDynamicBanner/")
    suspend fun getBanner(
        @Query("company") companyName: String,
        @Query("type") type: String,
        @Query("city") city: String,
    ): Response<BannerListDataModelNew>

    @GET("ss/api/v1/getWeeklyEarningsDistribution")
    suspend fun getWeeklyEarnings(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
    ): Response<WeeklyEarningsModel>

    @GET("ss/api/v1/paySlip")
    suspend fun getWeeklyPayslips(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
    ): Response<WEPayslipModel>

    @POST("/v2/app/referralHome")
    suspend fun getHomeReferralHomeData(
        @Body referralHomeRequestModel: ReferralHomeRequestModel
    ): Response<ReferralHomeRespModel>

    @POST("/v2/app/getReferralLinks")
    suspend fun sentLink(
        @Body referralHomeRequestModel: ReferralInviteRequestModel
    ): Response<ReferralInviteRspModel>

    @POST("/v2/app/referralStatus")
    suspend fun getReferralStatusModel(
        @Body referralHomeRequestModel: ReferralHomeRequestModel
    ): Response<ReferralHomeRespModel>

    @POST("/v3/app/referralHome")
    suspend fun getHomeReferralNewHomeData(
        @Body referralHomeRequestModel: ReferralHomeRequestModel
    ): Response<ReferralHomeNewRespModel>

    @POST("/v3/app/referralStatus")
    suspend fun getReferralStatusNewModel(
        @Body referralHomeRequestModel: ReferralHomeRequestModel
    ): Response<ReferralStatusNewModel>

    @POST("/v3/app/referralMilestone")
    suspend fun getReferralMilestoneModel(
        @Body referralHomeRequestModel: ReferralMilestoneRequestModel
    ): Response<ReferralMilestoneModel>

    @GET("ss/api/v1/getStaffingUserOrderLevelDetails")
    suspend fun getDailyOrders(
        @Query("date") date: String,
        @Query("company") company: String,
    ): Response<DailyOrderModel>

    @GET("/v2/app/fetchSavingDetails")
    suspend fun fetchDetails(): Response<FetchingUserInfoDTO>

    @POST("/v2/app/changeStatusForSavingsSystematicInstruction")
    suspend fun withDrawAmount(@Body data: WithDrawRequestModel): Response<WithDrawRespDTO>

    @POST("/v2/app/startSavingsSystematicInstruction")
    suspend fun applySaving(@Body data: ApplySavingRequestModel): Response<WithDrawRespDTO>

    @POST("/v3/app/getReferralLinks")
    suspend fun getReferralCode(@Body referralCode: ReferralCodeReqModel): Response<ReferralCodeRespDTO>

    @POST("auth/validateReferralCode")
    suspend fun validateReferralCode(@Body number: ValidateReferralRequestModel): Response<ReferralValidateCodeDTO>

    @GET("v2/app/userNudges")
    suspend fun getNudges(): Response<NudgesModel>

    @GET("/v2/app/fetchUserAttendanceDetails")
    suspend fun getFetchAttendance(): Response<AttendanceDetailsDTO>

    @POST("/v2/app/saveAttendanceForUser")
    suspend fun saveAttendanceForUser(@Body reqModel: ReqModelSaveAttendance): Response<SaveAttendanceDetailsDTO>

    @GET("/v2/app/weeklyEarningGoal")
    suspend fun showSaveGoal(@Query("amount") amount: String): Response<WeeklyGoalDTO>

    @GET("/v2/app/earningsHistoryReport")
    suspend fun getWeeklyHistory(): Response<WeeklyEarningHistoryDTO>

    @GET("/ss/api/v1/rateCard")
    suspend fun getAllRateCardDetails(): Response<RateCardDetailsDTO>

    @GET("jobseeker/documents")
    suspend fun getJMUploadDocs(
        @Query("siId") siId: String,
    ): Response<JMDocUploadDTO>

    @GET("jobseeker/getDynamicEntryContentVideo")
    suspend fun getDynamicEntryContentVideoList(): Response<DynamicEntryContentVideoModel>

    @POST("/v2/app/transactions/mplCredit")
    suspend fun mplCredit(@Body reqModel: ReqModelMPLCredit): Response<MPLResponseDTO>

    @POST("jobseeker/user/updateUserDetails")
    suspend fun postDocInpField(@Body reqModel: JMDocInputFieldPostDTO): Response<JMDocInputFieldResponseDTO>
}