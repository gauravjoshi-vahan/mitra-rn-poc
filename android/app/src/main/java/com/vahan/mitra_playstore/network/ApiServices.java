package com.vahan.mitra_playstore.network;


import com.google.gson.JsonObject;
import com.vahan.mitra_playstore.model.TranscInfoModel;
import com.vahan.mitra_playstore.model.cashoutClass;
import com.vahan.mitra_playstore.models.BannerListDataModel;
import com.vahan.mitra_playstore.models.BreakupModel;
import com.vahan.mitra_playstore.models.CheckCorrectIFSCModel;
import com.vahan.mitra_playstore.models.EarnDataModelJava;
import com.vahan.mitra_playstore.models.GetBankDetailsModel;
import com.vahan.mitra_playstore.models.InsuranceModel;
import com.vahan.mitra_playstore.models.LoanList;
import com.vahan.mitra_playstore.models.LoginModel;
import com.vahan.mitra_playstore.models.PayslipDataModel;
import com.vahan.mitra_playstore.models.PostLoanApiInfoModel;
import com.vahan.mitra_playstore.models.PostLoanApiModel;
import com.vahan.mitra_playstore.models.PostLoanApplyModel;
import com.vahan.mitra_playstore.models.SendOtp;
import com.vahan.mitra_playstore.models.TransactionDetailsFilterConstraintsModel;
import com.vahan.mitra_playstore.models.TransactionDetailsModelJava;
import com.vahan.mitra_playstore.models.UploadDocDTO;
import com.vahan.mitra_playstore.models.UserResponse;
import com.vahan.mitra_playstore.models.VerificationResponseModel;
import com.vahan.mitra_playstore.models.VerifyModel;
import com.vahan.mitra_playstore.models.kotlin.GetPurPosesAndAmountModel;
import com.vahan.mitra_playstore.utils.Constants;
import com.vahan.mitra_playstore.view.earn.model.NudgesModel;
import com.vahan.mitra_playstore.view.jobsmarketplace.docupload.datamodels.JMDocUploadDTO;
import com.vahan.mitra_playstore.view.jobsmarketplace.docupload.datamodels.JMPostDocDTO;
import com.vahan.mitra_playstore.view.supporttickets.datamodels.SupportTicketDTO;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiServices {

    @POST("/auth/sendOtp")
    Call<SendOtp> sendOtp(@Body JsonObject otp);

    @POST("/auth/login")
    Call<LoginModel> login(@Body JsonObject otp);

    @Multipart
    @POST("/upload")
    Call<UploadDocDTO> upload(@Part MultipartBody.Part file);

    @POST("/user-documents/verify")
    Call<VerificationResponseModel> verifyAadharCard(@Body JsonObject source);

    @GET("v2/app/profile")
    Call<EarnDataModelJava> getEarnList();

    @POST("v2/app/bank_account")
    Call<VerifyModel> createAccount(@Body JsonObject jsonObject);

    @GET("/app/bank_account")
    Call<GetBankDetailsModel> getBankInfo();

    @GET("/" + "{" + Constants.IFSCCODEKEY + "}")
    Call<CheckCorrectIFSCModel> getIFSCDetails(
            @Path(Constants.IFSCCODEKEY) String ifscNumber
    );

    @GET("app/payroll-earnings/" + "{" + Constants.EARNINGID + "}")
    Call<BreakupModel> getBreakupInfo(@Path(Constants.EARNINGID) String id);

    @GET("v2/app/insurance")
    Call<InsuranceModel> getInsuranceDetails();

    @POST("/v2/app/transactions")
    Call<TransactionDetailsModelJava> getTransactionDetails(@Body JsonObject jsonObject);

    @GET("/v2/app/transactions/constraints")
    Call<TransactionDetailsFilterConstraintsModel> getTransactionDetailsFilterConstraints();

    @GET("/v2/app/transactions/{transactionId}/info")
    Call<TranscInfoModel> getTransactionDetailInfo(@Path("transactionId") String value);

    @GET("/v2/app/transactions/constraints")
    Call<TransactionDetailsFilterConstraintsModel> getTransctionDetailsFilterConstraints();

    @POST("v2/app/transactions/cashout")
    Call<cashoutClass> paymeMoney(@Body JsonObject jsonObject);

    @GET("v2/app/payout_details/{startPage}/{totalPage}")
    Call<PayslipDataModel> getPayslip(
            @Path("startPage") String startPage,
            @Path("totalPage") String totalPage
    );

    @GET("v2/app/loan/purposesAndAmounts")
    Call<GetPurPosesAndAmountModel> getPurPosesAndAmount();

    @POST("/v2/app/loan/variables")
    Call<List<PostLoanApiModel>> postLoanResponse(@Body JsonObject jsonObject);

    @POST("/v2/app/loan/summary")
    Call<PostLoanApiInfoModel> postLoanApiInfo(@Body JsonObject jsonObject);

    @POST("/v2/app/loan/apply")
    Call<PostLoanApplyModel> postApplyLoan(@Body JsonObject jsonObject);

    @GET("/v2/app/loan/list")
    Call<LoanList> getLoanList();

    @GET("/v2/app/filterDynamicBanner/")
    Call<BannerListDataModel> getBanner(@Query("company") String companyName, @Query("type") String type);

    @GET("auth/userType")
    Call<UserResponse> getData();

    @POST("/meer/sms-store")
    Call<Void> postSMSInfo(@Body JsonObject jsonObject);

    @POST("/meer/device-info-store")
    Call<Void> postDeviceInfo(@Body JsonObject jsonObject);

    @POST("/meer/notification-store")
    Call<Void> postNotificationInfo(@Body JsonObject jsonObject);

    @GET("/app/documents")
    Call<EarnDataModelJava.Documents> getNonPayrollDocs();

    @GET("/api/v2/search/tickets")
    Call<SupportTicketDTO> callFreshDeskAPI(@Query("query") String queryText);

    @GET("/ss/api/v1/nudgeDetails")
    Call<NudgesModel> getNudges();

    @GET("jobseeker/documents")
    Call<JMDocUploadDTO> getJMUploadDocs(@Query("siId") String siId);

    @POST("jobseeker/documents")
    Call<JMPostDocDTO> postJMUploadDocs(@Body JsonObject jsonObject);
}
