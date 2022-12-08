package com.vahan.mitra_playstore.network;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.JsonObject;
import com.vahan.mitra_playstore.model.TranscInfoModel;
import com.vahan.mitra_playstore.models.BreakupModel;
import com.vahan.mitra_playstore.models.CheckCorrectIFSCModel;
import com.vahan.mitra_playstore.models.EarnDataModelJava;
import com.vahan.mitra_playstore.models.GetBankDetailsModel;
import com.vahan.mitra_playstore.models.InsuranceModel;
import com.vahan.mitra_playstore.models.LoanList;
import com.vahan.mitra_playstore.models.LoginModel;
import com.vahan.mitra_playstore.models.PostLoanApiInfoModel;
import com.vahan.mitra_playstore.models.PostLoanApiModel;
import com.vahan.mitra_playstore.models.PostLoanApplyModel;
import com.vahan.mitra_playstore.models.SendOtp;
import com.vahan.mitra_playstore.models.TransactionDetailsFilterConstraintsModel;
import com.vahan.mitra_playstore.models.TransactionDetailsModelJava;
import com.vahan.mitra_playstore.models.UserResponse;
import com.vahan.mitra_playstore.models.VerificationResponseModel;
import com.vahan.mitra_playstore.models.VerifyModel;
import com.vahan.mitra_playstore.models.kotlin.GetPurPosesAndAmountModel;
import com.vahan.mitra_playstore.view.supporttickets.datamodels.SupportTicketDTO;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * API SETUP CLASS USING VIEW_MODEL WORK AS A SHARED VIEW_MODEL
 */

public class SharedViewModel extends ViewModel {
    public MutableLiveData<SendOtp> sendOtp(JsonObject number) {
        return new Retrofithit().sendOtp(number);
    }

    public MutableLiveData<LoginModel> login(JsonObject jsonObject) {
        return new Retrofithit().login(jsonObject);
    }


    public MutableLiveData<String> uploadImageFile(Context uploadActivity, String token, Bitmap data, String filename) {
        return new Retrofithit().uploadImageFile(uploadActivity, token, data, filename);
    }


    public MutableLiveData<VerificationResponseModel> verifyAadharCard(Context context, JsonObject verifyModel) {
        return new Retrofithit().verifyAadharCard(context, verifyModel);
    }

    public MutableLiveData<EarnDataModelJava> getEarnList() {
        return new Retrofithit().getEarnList();
    }

    public MutableLiveData<VerifyModel> createAccount(JsonObject jsonObject) {
        return new Retrofithit().createAccount(jsonObject);
    }

    public MutableLiveData<GetBankDetailsModel> getBankInfo() {
        return new Retrofithit().getBankInfo();
    }

    public MutableLiveData<CheckCorrectIFSCModel> checkCorrectIfsc(String ifscNumber) {
        return new Retrofithit().getCheckIfscNumber(ifscNumber);
    }

    public MutableLiveData<BreakupModel> getBreakupInfo(String id) {
        return new Retrofithit().getBreakupInfo(id);
    }

    public MutableLiveData<InsuranceModel> checkForInsuranceEligible() {
        return new Retrofithit().getInsurance();
    }

    public LiveData<TransactionDetailsFilterConstraintsModel> getTransactionDetailsFilterConstraints() {
        return new Retrofithit().getTransactionDetailsFilterConstraints();
    }

    public LiveData<TranscInfoModel> getTransactionDetailsInfo(String id) {
        return new Retrofithit().getTransactionDetailsInfo(id);
    }

    public LiveData<GetPurPosesAndAmountModel> getPurPosesAndAmount() {
        return new Retrofithit().getPurPosesAndAmount();
    }

    public LiveData<List<PostLoanApiModel>> postLoanApiModel(JsonObject jsonObject) {
        return new Retrofithit().postLoanApiModel(jsonObject);
    }

    public LiveData<PostLoanApiInfoModel> postLoanApiInfo(@NotNull JsonObject jsonObject) {
        return new Retrofithit().postLoanApiInfo(jsonObject);
    }

    public LiveData<LoanList> getLoanList() {
        return new Retrofithit().getLoanList();
    }

    public LiveData<PostLoanApplyModel> postApplyLoan(@NotNull JsonObject jsonObject) {
        return new Retrofithit().postApplyLoan(jsonObject);
    }

    public LiveData<TransactionDetailsModelJava> getTransactionDetails(JsonObject data) {
        return new Retrofithit().getTransactionDetails(data);
    }

    public LiveData<UserResponse> getUserData() {
        return new Retrofithit().getUserResponse();
    }

    public LiveData<EarnDataModelJava.Documents> getNonPayrollDocs() {
        return new Retrofithit().getNonPayrollDocs();
    }

    public MutableLiveData<SupportTicketDTO> callFreshDeskAPI(String queryText) {
        return new Retrofithit().callFreshDeskAPI(queryText);
    }

}
