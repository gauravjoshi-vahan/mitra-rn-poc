package com.vahan.mitra_playstore.network;


import android.content.Context;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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
import com.vahan.mitra_playstore.models.TransactionDetailInfoModel;
import com.vahan.mitra_playstore.models.TransactionDetailsFilterConstraintsModel;
import com.vahan.mitra_playstore.models.TransactionDetailsModelJava;
import com.vahan.mitra_playstore.models.UserResponse;
import com.vahan.mitra_playstore.models.VerificationResponseModel;
import com.vahan.mitra_playstore.models.VerifyModel;
import com.vahan.mitra_playstore.models.kotlin.GetPurPosesAndAmountModel;
import com.vahan.mitra_playstore.utils.Constants;
import com.vahan.mitra_playstore.utils.PrefrenceUtils;
import com.vahan.mitra_playstore.view.supporttickets.datamodels.SupportTicketDTO;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ALL")
public class Retrofithit {
    public MutableLiveData mutableLiveSendOtp = new MutableLiveData<SendOtp>();
    public MutableLiveData mutableLivelogin = new MutableLiveData<LoginModel>();
    public MutableLiveData mutableLiveUpload = new MutableLiveData<String>();
    public MutableLiveData mutableVerifyPhoto = new MutableLiveData<String>();
    public MutableLiveData mutableEarnModel = new MutableLiveData<EarnDataModelJava>();
    public MutableLiveData mutableCreateAccount = new MutableLiveData<VerifyModel>();
    public MutableLiveData mutableGetBankAccount = new MutableLiveData<GetBankDetailsModel>();
    public MutableLiveData mutableGetIFSCDetails = new MutableLiveData<CheckCorrectIFSCModel>();
    public MutableLiveData mutableBreakupData = new MutableLiveData<BreakupModel>();
    public MutableLiveData mutableInsuranceData = new MutableLiveData<InsuranceModel>();
    public MutableLiveData mutableLiveTransaction = new MutableLiveData<TransactionDetailsModelJava>();
    public MutableLiveData mutableLiveTransactionDetailsConstraints = new MutableLiveData<TransactionDetailsFilterConstraintsModel>();
    public MutableLiveData mutableLiveDataTransactionInfo = new MutableLiveData<TransactionDetailInfoModel>();
    public MutableLiveData mutableLiveCashout = new MutableLiveData<cashoutClass>();
    public MutableLiveData mutableLiveDataPayslip = new MutableLiveData<PayslipDataModel>();
    public MutableLiveData mutableLiveDataPurposeAmount = new MutableLiveData<GetPurPosesAndAmountModel>();
    public MutableLiveData mutableLiveDataPostAmountLoan = new MutableLiveData<List<PostLoanApiModel>>();
    public MutableLiveData mutableLiveDataPostInfoLoan = new MutableLiveData<PostLoanApiInfoModel>();
    public MutableLiveData mutableLivePostApplyLoan = new MutableLiveData<PostLoanApplyModel>();
    public MutableLiveData mutableGetLoanList = new MutableLiveData<LoanList>();
    public MutableLiveData mutablebannerData = new MutableLiveData<BannerListDataModel>();
    public MutableLiveData mutableUserata = new MutableLiveData<UserResponse>();
    public MutableLiveData mutabletLiveSmsData = new MutableLiveData<String>();
    public MutableLiveData mutableNonPayrollDocsData = new MutableLiveData<EarnDataModelJava.Documents>();
    public MutableLiveData mutableFreshDeskData = new MutableLiveData<SupportTicketDTO>();


    public MutableLiveData<SendOtp> sendOtp(JsonObject mobileNumber) {
        Call<SendOtp> call = new RetrofitClient().getApiRetrofitOnlyDeviceId().sendOtp(mobileNumber);
        call.enqueue(new Callback<SendOtp>() {
            @Override
            public void onResponse(Call<SendOtp> call, Response<SendOtp> response) {
                try {
                    if (response.code() >= 200 && response.code() <= 299) {
                        mutableLiveSendOtp.postValue(response.body());
                    } else if (response.code() >= 399) {
                        SendOtp otp = new SendOtp();
                        otp.setMessage("fail");
                        otp.setStatusCode(response.code());
                        otp.setSuccess(false);
                        mutableLiveSendOtp.postValue(otp);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SendOtp> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    SendOtp otp = new SendOtp();
                    otp.setMessage("Socket Time out. Please try again.");
                    otp.setSuccess(false);
                    mutableLiveSendOtp.postValue(otp);
                } else {
                    SendOtp otp = new SendOtp();
                    otp.setMessage("Something went wrong");
                    otp.setSuccess(false);
                    mutableLiveSendOtp.postValue(otp);
                }
            }
        });
        return mutableLiveSendOtp;
    }

    public MutableLiveData<LoginModel> login(JsonObject jsonObject) {
        Call<LoginModel> call = new RetrofitClient().getApiRetrofitOnlyDeviceId().login(jsonObject);
        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                try {
                    if (response.code() >= 200 && response.code() <= 299) {
                        mutableLivelogin.postValue(response.body());
                    } else if (response.code() >= 400) {
                        LoginModel model = new LoginModel();
                        model.setSuccess(false);
                        mutableLivelogin.postValue(model);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    LoginModel model = new LoginModel();
                    model.setSuccess(false);
                    mutableLiveSendOtp.postValue(model);
                }
            }
        });
        return mutableLivelogin;
    }

    public MutableLiveData<String> uploadImageFile(Context uploadActivity, String token, Bitmap data, String filename) {

        try {
            File f = new File(uploadActivity.getCacheDir(), filename + ".jpg");
            f.createNewFile();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            data.compress(Bitmap.CompressFormat.PNG, 50, baos);
            byte[] b = baos.toByteArray();
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(b);
            fos.flush();
            fos.close();

            OkHttpClient client = new OkHttpClient();
            client = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.MINUTES)
                    .writeTimeout(5, TimeUnit.MINUTES)
                    .readTimeout(5, TimeUnit.MINUTES)
                    .build();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", f.toString(),
                            RequestBody.create(MediaType.parse("image/png"), f))
                    .build();

            Request request = new Request.Builder()
                    .post(requestBody)
                    .url(Constants.BASE_URL + "/upload")
                    //.url("https://stg-api.mitra.vahan.co/upload")
                    .addHeader("Timestamp", String.valueOf(System.currentTimeMillis()))
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("Authorization", token)
                    .addHeader("app-build", "signed")
                    .addHeader("device_id", PrefrenceUtils.retriveData(uploadActivity, Constants.DEVICE_ID))
                    .build();

            okhttp3.Call call = client.newCall(request);
            call.enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {

                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject object = new JSONObject(response.body().string());
                            mutableLiveUpload.postValue(object.getString("fileURL"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mutableLiveUpload;
    }

    public MutableLiveData<VerificationResponseModel> verifyAadharCard(Context context, JsonObject source) {
        Call<VerificationResponseModel> call = new RetrofitClient().getApiRetrofitInterceptor().verifyAadharCard(source);
        call.enqueue(new Callback<VerificationResponseModel>() {
            @Override
            public void onResponse(Call<VerificationResponseModel> call, Response<VerificationResponseModel> response) {
                try {
                    VerificationResponseModel responseModel = new VerificationResponseModel();
                    if (response.code() >= 200 && response.code() <= 299) {
                        mutableVerifyPhoto.postValue(response.body());
                    } else if (response.code() >= 400 && response.code() <= 499) {
                        responseModel.setStatusCode(response.code());
                        mutableVerifyPhoto.postValue(responseModel);
                    } else if (response.code() >= 500) {
                        responseModel.setStatusCode(500);
                        mutableVerifyPhoto.postValue(responseModel);
                    }
                } catch (Exception e) {
                    VerificationResponseModel responseModel = new VerificationResponseModel();
                    responseModel.setStatusCode(500);
                    mutableVerifyPhoto.postValue(responseModel);
                }
            }

            @Override
            public void onFailure(Call<VerificationResponseModel> call, Throwable t) {
                PrefrenceUtils.insertData(context, Constants.AADHARCARDFRONT, "");
                PrefrenceUtils.insertData(context, Constants.AADHARCARDBACK, "");
                VerificationResponseModel responseModel = new VerificationResponseModel();
                responseModel.setStatusCode(500);
                mutableVerifyPhoto.postValue(responseModel);
            }
        });
        return mutableVerifyPhoto;
    }

    public MutableLiveData<EarnDataModelJava> getEarnList() {
        Call<EarnDataModelJava> call = new RetrofitClient().getApiRetrofitInterceptor().getEarnList();
        call.enqueue(new Callback<EarnDataModelJava>() {
            @Override
            public void onResponse(Call<EarnDataModelJava> call, Response<EarnDataModelJava> response) {
                try {
                    EarnDataModelJava dataModel = new EarnDataModelJava();
                    if (response.code() >= 200 && response.code() <= 299) {
                        mutableEarnModel.postValue(response.body());
                    } else if (response.code() >= 400 && response.code() <= 499) {
                        dataModel.setStatusCode(response.code());
                        mutableEarnModel.postValue(dataModel);
                    } else if (response.code() >= 500) {
                        dataModel.setStatusCode(500);
                        mutableEarnModel.postValue(dataModel);
                    } else {
                        dataModel.setStatusCode(500);
                        mutableEarnModel.postValue(dataModel);
                    }
                } catch (Exception e) {
                    EarnDataModelJava dataModel = new EarnDataModelJava();
                    dataModel.setStatusCode(500);
                    mutableEarnModel.postValue(dataModel);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<EarnDataModelJava> call, Throwable t) {
                EarnDataModelJava dataModel = new EarnDataModelJava();
                dataModel.setStatusCode(500);
                mutableEarnModel.postValue(dataModel);
            }
        });
        return mutableEarnModel;
    }

    public MutableLiveData<VerifyModel> createAccount(JsonObject jsonObject) {
        Call<VerifyModel> call = new RetrofitClient().getApiRetrofitInterceptor().createAccount(jsonObject);
        call.enqueue(new Callback<VerifyModel>() {
            @Override
            public void onResponse(Call<VerifyModel> call, Response<VerifyModel> response) {
                try {
                    if (response.code() >= 200 && response.code() <= 299) {
                        mutableCreateAccount.postValue(response.body());
                    }
                    if (response.code() >= 500) {
                        VerifyModel model = new VerifyModel();
                        model.setStatus("Failed");
                        model.setMessage("Please check all the account details");
                        mutableCreateAccount.postValue(model);
                    }
                    if (response.code() >= 400 && response.code() <= 499) {
                        VerifyModel model = new VerifyModel();
                        model.setStatus("Failed");
                        model.setMessage("Please check all the account details");
                        mutableCreateAccount.postValue(model);
                    }
                } catch (Exception e) {
                    VerifyModel model = new VerifyModel();
                    model.setStatus("Failed");
                    model.setMessage("Please check all the account details");
                    mutableCreateAccount.postValue(model);
                }
            }

            @Override
            public void onFailure(Call<VerifyModel> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    VerifyModel model = new VerifyModel();
                    model.setStatus("Failed");
                    model.setMessage("Please check all the account details");
                    mutableCreateAccount.postValue(model);
                }
            }
        });
        return mutableCreateAccount;
    }

    public MutableLiveData<GetBankDetailsModel> getBankInfo() {
        Call<GetBankDetailsModel> call = new RetrofitClient().getApiRetrofitInterceptor().getBankInfo();
        call.enqueue(new Callback<GetBankDetailsModel>() {
            @Override
            public void onResponse(Call<GetBankDetailsModel> call, Response<GetBankDetailsModel> response) {
                try {
                    if (response.code() >= 200 && response.code() <= 299) {
                        mutableGetBankAccount.postValue(response.body());
                    } else if (response.code() >= 400 && response.code() <= 499) {
                        GetBankDetailsModel detailsModel = new GetBankDetailsModel();
                        detailsModel.getBankAccountDetails().setBankName("Unauthorized");
                        mutableGetBankAccount.postValue(detailsModel);
                    } else if (response.code() >= 500) {
                        GetBankDetailsModel detailsModel = new GetBankDetailsModel();
                        detailsModel.setStatusCode(500);
                        mutableGetBankAccount.postValue(detailsModel);
                    }
                } catch (Exception e) {
                    GetBankDetailsModel detailsModel = new GetBankDetailsModel();
                    detailsModel.setStatusCode(500);
                    mutableGetBankAccount.postValue(detailsModel);
                }
            }

            @Override
            public void onFailure(Call<GetBankDetailsModel> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    GetBankDetailsModel detailsModel = new GetBankDetailsModel();
                    detailsModel.setStatusCode(500);
                    mutableGetBankAccount.postValue(detailsModel);
                }
            }
        });
        return mutableGetBankAccount;
    }

    public MutableLiveData<CheckCorrectIFSCModel> getCheckIfscNumber(String ifscNumber) {
        Call<CheckCorrectIFSCModel> call = new RetrofitClient().razorPayIntegration().getIFSCDetails(ifscNumber);
        call.enqueue(new Callback<CheckCorrectIFSCModel>() {
            @Override
            public void onResponse(Call<CheckCorrectIFSCModel> call, Response<CheckCorrectIFSCModel> response) {
                CheckCorrectIFSCModel checkCorrectIFSCModel = new CheckCorrectIFSCModel();
                try {
                    if (response.code() >= 200 && response.code() <= 299) {
                        mutableGetIFSCDetails.postValue(response.body());
                    } else if (response.code() >= 400 && response.code() <= 511) {
                        checkCorrectIFSCModel.setBank("Not Found");
                        mutableGetIFSCDetails.postValue(checkCorrectIFSCModel);
                    }
                } catch (Exception e) {
                    checkCorrectIFSCModel.setBank("Not Found");
                    mutableGetIFSCDetails.postValue(checkCorrectIFSCModel);
                }
            }

            @Override
            public void onFailure(Call<CheckCorrectIFSCModel> call, Throwable t) {
                CheckCorrectIFSCModel checkCorrectIFSCModel = new CheckCorrectIFSCModel();
                checkCorrectIFSCModel.setBank("Not Found");
                mutableGetIFSCDetails.postValue(checkCorrectIFSCModel);
            }
        });
        return mutableGetIFSCDetails;
    }

    public MutableLiveData<BreakupModel> getBreakupInfo(String id) {
        Call<BreakupModel> call = new RetrofitClient().getApiRetrofitInterceptor().getBreakupInfo(id);
        call.enqueue(new Callback<BreakupModel>() {
            @Override
            public void onResponse(Call<BreakupModel> call, Response<BreakupModel> response) {
                try {
                    BreakupModel breakupModel = new BreakupModel();
                    if (response.code() >= 200 && response.code() <= 299) {
                        mutableBreakupData.postValue(response.body());
                    } else if (response.code() >= 400 && response.code() <= 499) {
                        breakupModel.setStatusCode(response.code());
                        mutableBreakupData.postValue(breakupModel);
                    } else if (response.code() >= 500) {
                        breakupModel.setStatusCode(500);
                        mutableBreakupData.postValue(breakupModel);
                    }
                } catch (Exception e) {
                    BreakupModel breakupModel = new BreakupModel();
                    breakupModel.setStatusCode(500);
                    mutableBreakupData.postValue(breakupModel);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<BreakupModel> call, Throwable t) {
                BreakupModel breakupModel = new BreakupModel();
                breakupModel.setStatusCode(500);
                mutableBreakupData.postValue(breakupModel);
            }
        });
        return mutableBreakupData;
    }

    public MutableLiveData<InsuranceModel> getInsurance() {
        Call<InsuranceModel> call = new RetrofitClient().getApiRetrofitInterceptor().getInsuranceDetails();
        call.enqueue(new Callback<InsuranceModel>() {
            @Override
            public void onResponse(Call<InsuranceModel> call, Response<InsuranceModel> response) {
                try {
                    InsuranceModel insuranceModel = new InsuranceModel();
                    if (response.code() >= 200 && response.code() <= 299) {
                        mutableInsuranceData.postValue(response.body());
                    } else if (response.code() >= 400 && response.code() <= 499) {
                        insuranceModel.setStatusCode(response.code());
                        mutableInsuranceData.postValue(insuranceModel);
                    } else if (response.code() >= 500) {
                        insuranceModel.setStatusCode(500);
                        mutableInsuranceData.postValue(insuranceModel);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<InsuranceModel> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    InsuranceModel insuranceModel = new InsuranceModel();
                    insuranceModel.setStatusCode(500);
                    mutableInsuranceData.postValue(insuranceModel);
                }
            }
        });
        return mutableInsuranceData;
    }

    public MutableLiveData<TransactionDetailsModelJava> getTransactionDetails(JsonObject data) {
        Call<TransactionDetailsModelJava> call = new RetrofitClient().getApiRetrofitInterceptor().getTransactionDetails(data);
        call.enqueue(new Callback<TransactionDetailsModelJava>() {
            @Override
            public void onResponse(Call<TransactionDetailsModelJava> call, Response<TransactionDetailsModelJava> response) {
                try {
                    TransactionDetailsModelJava dataModel = new TransactionDetailsModelJava();
                    if (response.code() >= 200 && response.code() <= 299) {
                        mutableLiveTransaction.postValue(response.body());
                    } else if (response.code() >= 400 && response.code() <= 499) {
                        dataModel.setStatusCode(response.code());
                        mutableLiveTransaction.postValue(dataModel);
                    } else if (response.code() >= 500) {
                        dataModel.setStatusCode(500);
                        mutableLiveTransaction.postValue(dataModel);
                    } else {
                        dataModel.setStatusCode(500);
                        mutableLiveTransaction.postValue(dataModel);
                    }
                } catch (Exception e) {
                    TransactionDetailsModelJava dataModel = new TransactionDetailsModelJava();
                    dataModel.setStatusCode(500);
                    mutableLiveTransaction.postValue(dataModel);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<TransactionDetailsModelJava> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    TransactionDetailsModelJava dataModel = new TransactionDetailsModelJava();
                    dataModel.setStatusCode(500);
                    mutableLiveTransaction.postValue(dataModel);
                }
            }
        });
        return mutableLiveTransaction;
    }

    public MutableLiveData<TransactionDetailsFilterConstraintsModel> getTransactionDetailsFilterConstraints() {
        Call<TransactionDetailsFilterConstraintsModel> call = new RetrofitClient().getApiRetrofitInterceptor().getTransactionDetailsFilterConstraints();
        call.enqueue(new Callback<TransactionDetailsFilterConstraintsModel>() {
            @Override
            public void onResponse(Call<TransactionDetailsFilterConstraintsModel> call, Response<TransactionDetailsFilterConstraintsModel> response) {
                try {
                    TransactionDetailsFilterConstraintsModel filterConstraintsModel = new TransactionDetailsFilterConstraintsModel();
                    if (response.code() >= 200 && response.code() <= 299) {
                        mutableLiveTransactionDetailsConstraints.postValue(response.body());
                    } else if (response.code() >= 400 && response.code() <= 499) {
                        filterConstraintsModel.setStatusCode(response.code());
                        mutableLiveTransactionDetailsConstraints.postValue(filterConstraintsModel);
                    } else if (response.code() >= 500) {
                        filterConstraintsModel.setStatusCode(500);
                        mutableLiveTransactionDetailsConstraints.postValue(filterConstraintsModel);
                    }
                } catch (Exception e) {
                    TransactionDetailsFilterConstraintsModel filterConstraintsModel = new TransactionDetailsFilterConstraintsModel();
                    filterConstraintsModel.setStatusCode(500);
                    mutableLiveTransactionDetailsConstraints.postValue(filterConstraintsModel);
                }
            }

            @Override
            public void onFailure(Call<TransactionDetailsFilterConstraintsModel> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    TransactionDetailsFilterConstraintsModel filterConstraintsModel = new TransactionDetailsFilterConstraintsModel();
                    filterConstraintsModel.setStatusCode(500);
                    mutableLiveTransactionDetailsConstraints.postValue(filterConstraintsModel);
                }
            }
        });
        return mutableLiveTransactionDetailsConstraints;
    }

    public LiveData<TranscInfoModel> getTransactionDetailsInfo(String id) {
        Call<TranscInfoModel> call = new RetrofitClient().getApiRetrofitInterceptor().getTransactionDetailInfo(id);
        call.enqueue(new Callback<TranscInfoModel>() {
            @Override
            public void onResponse(Call<TranscInfoModel> call, Response<TranscInfoModel> response) {
                try {

                    if (response.code() >= 200 && response.code() <= 299) {
                        mutableLiveDataTransactionInfo.postValue(response.body());
                    } else if (response.code() >= 400 && response.code() <= 499) {
                        TranscInfoModel infoModel = new TranscInfoModel(
                                "", response.code(), "", "", "",
                                0, "", "", "", "", "", "", "", "", "",
                                "", "", null, "");
                        infoModel.setStatusCode(response.code());
                    } else if (response.code() >= 500) {
                        TranscInfoModel infoModel = new TranscInfoModel(
                                "", response.code(), "", "", "",
                                0, "", "", "", "", "", "", "", "", "",
                                "", "", null, "");
                        infoModel.setStatusCode(500);
                    }
                } catch (Exception e) {
                    TranscInfoModel infoModel = new TranscInfoModel(
                            "", 500, "", "", "",
                            0, "", "", "", "", "", "", "", "", "",
                            "", "", null, "");
                    infoModel.setStatusCode(500);
                }
            }

            @Override
            public void onFailure(Call<TranscInfoModel> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    TranscInfoModel infoModel = new TranscInfoModel(
                            "", 500, "", "", "",
                            0, "", "", "", "", "", "", "", "", "",
                            "", "", null, "");
                    infoModel.setStatusCode(500);
                }
            }
        });
        return mutableLiveDataTransactionInfo;
    }

    public MutableLiveData<cashoutClass> paymeMoney(String amount, String purpose) {

        JsonObject amountInPaisa = new JsonObject();
        amountInPaisa.addProperty("amount", Double.parseDouble(amount));
        amountInPaisa.addProperty("cashoutPurpose", purpose);
        Call<cashoutClass> call = new RetrofitClient().getApiRetrofitInterceptor().paymeMoney(amountInPaisa);
        call.enqueue(new Callback<cashoutClass>() {
            @Override
            public void onResponse(Call<cashoutClass> call, Response<cashoutClass> response) {
                try {
                    if (response.code() >= 200 && response.code() <= 299) {
                        mutableLiveCashout.postValue(response.body());
                    }
                    if (response.code() >= 400 && response.code() <= 499) {
                        cashoutClass cashoutClass = new cashoutClass(0, "", false);
                        mutableLiveCashout.postValue(cashoutClass);
                    } else if (response.code() >= 500) {
                        cashoutClass cashoutClass = new cashoutClass(0, "", false);
                        mutableLiveCashout.postValue(cashoutClass);
                    }
                } catch (Exception e) {
                    cashoutClass cashoutClass = new cashoutClass(0, "", false);
                    mutableLiveCashout.postValue(cashoutClass);
                }
            }

            @Override
            public void onFailure(Call<cashoutClass> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    cashoutClass cashoutClass = new cashoutClass(0, "", false);
                    mutableLiveCashout.postValue(cashoutClass);
                }
            }
        });
        return mutableLiveCashout;
    }


    public MutableLiveData<GetPurPosesAndAmountModel> getPurPosesAndAmount() {
        Call<GetPurPosesAndAmountModel> call = new RetrofitClient().getApiRetrofitInterceptor().getPurPosesAndAmount();
        call.enqueue(new Callback<GetPurPosesAndAmountModel>() {
            @Override
            public void onResponse(Call<GetPurPosesAndAmountModel> call, Response<GetPurPosesAndAmountModel> response) {
                try {
                    GetPurPosesAndAmountModel getPurPosesAndAmountModel = new GetPurPosesAndAmountModel();
                    if (response.code() >= 200 && response.code() <= 299) {
                        mutableLiveDataPurposeAmount.postValue(response.body());
                    } else if (response.code() >= 400 && response.code() <= 499) {
                        getPurPosesAndAmountModel.setStatusCode(response.code());
                        mutableLiveDataPurposeAmount.postValue(getPurPosesAndAmountModel);
                    } else if (response.code() >= 500) {
                        getPurPosesAndAmountModel.setStatusCode(500);
                        mutableLiveDataPurposeAmount.postValue(getPurPosesAndAmountModel);
                    }
                } catch (Exception e) {
                    GetPurPosesAndAmountModel getPurPosesAndAmountModel = new GetPurPosesAndAmountModel();
                    getPurPosesAndAmountModel.setStatusCode(500);
                    mutableLiveDataPurposeAmount.postValue(getPurPosesAndAmountModel);
                }
            }

            @Override
            public void onFailure(Call<GetPurPosesAndAmountModel> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    GetPurPosesAndAmountModel getPurPosesAndAmountModel = new GetPurPosesAndAmountModel();
                    getPurPosesAndAmountModel.setStatusCode(500);
                    mutableLiveDataPurposeAmount.postValue(getPurPosesAndAmountModel);
                }
            }
        });
        return mutableLiveDataPurposeAmount;
    }

    public LiveData<List<PostLoanApiModel>> postLoanApiModel(JsonObject jsonObject) {
        Call<List<PostLoanApiModel>> call = new RetrofitClient().getApiRetrofitInterceptor().postLoanResponse(jsonObject);
        call.enqueue(new Callback<List<PostLoanApiModel>>() {
            @Override
            public void onResponse(Call<List<PostLoanApiModel>> call, Response<List<PostLoanApiModel>> response) {
                try {
                    PostLoanApiModel apiInfoModel = new PostLoanApiModel();
                    if (response.code() >= 200 && response.code() <= 299) {
                        mutableLiveDataPostAmountLoan.postValue(response.body());
                    } else if (response.code() >= 400 && response.code() <= 499) {
                        apiInfoModel.setStatusCode(response.code());
                        mutableLiveDataPostAmountLoan.postValue(apiInfoModel);
                    } else if (response.code() >= 500) {
                        apiInfoModel.setStatusCode(500);
                        mutableLiveDataPostAmountLoan.postValue(apiInfoModel);
                    }
                } catch (Exception e) {
                    PostLoanApiModel apiInfoModel = new PostLoanApiModel();
                    apiInfoModel.setStatusCode(500);
                    mutableLiveDataPostAmountLoan.postValue(apiInfoModel);
                }
            }

            @Override
            public void onFailure(Call<List<PostLoanApiModel>> call, Throwable t) {
                if (t instanceof SocketTimeoutException) {
                    PostLoanApiModel apiInfoModel = new PostLoanApiModel();
                    apiInfoModel.setStatusCode(500);
                    mutableLiveDataPostAmountLoan.postValue(apiInfoModel);
                }
            }
        });
        return mutableLiveDataPostAmountLoan;
    }

    public LiveData<PostLoanApiInfoModel> postLoanApiInfo(JsonObject jsonObject) {
        Call<PostLoanApiInfoModel> call = new RetrofitClient().getApiRetrofitInterceptor().postLoanApiInfo(jsonObject);
        call.enqueue(new Callback<PostLoanApiInfoModel>() {
            @Override
            public void onResponse(Call<PostLoanApiInfoModel> call, Response<PostLoanApiInfoModel> response) {
                try {
                    PostLoanApiInfoModel apiInfoModel = new PostLoanApiInfoModel();
                    if (response.code() >= 200 && response.code() <= 299) {
                        mutableLiveDataPostInfoLoan.postValue(response.body());
                    } else if (response.code() >= 400 && response.code() <= 499) {
                        apiInfoModel.setStatusCode(response.code());
                        mutableLiveDataPostInfoLoan.postValue(apiInfoModel);
                    } else if (response.code() >= 500) {
                        apiInfoModel.setStatusCode(500);
                        mutableLiveDataPostInfoLoan.postValue(apiInfoModel);
                    }
                } catch (Exception e) {
                    PostLoanApiInfoModel apiInfoModel = new PostLoanApiInfoModel();
                    apiInfoModel.setStatusCode(500);
                    mutableLiveDataPostInfoLoan.postValue(apiInfoModel);
                }
            }

            @Override
            public void onFailure(Call<PostLoanApiInfoModel> call, Throwable t) {
                PostLoanApiInfoModel apiInfoModel = new PostLoanApiInfoModel();
                apiInfoModel.setStatusCode(500);
                mutableLiveDataPostInfoLoan.postValue(apiInfoModel);
            }
        });
        return mutableLiveDataPostInfoLoan;
    }

    public LiveData<PostLoanApplyModel> postApplyLoan(JsonObject jsonObject) {
        Call<PostLoanApplyModel> call = new RetrofitClient().getApiRetrofitInterceptor().postApplyLoan(jsonObject);
        call.enqueue(new Callback<PostLoanApplyModel>() {
            @Override
            public void onResponse(Call<PostLoanApplyModel> call, Response<PostLoanApplyModel> response) {
                try {
                    PostLoanApplyModel apiInfoModel = new PostLoanApplyModel();
                    if (response.code() >= 200 && response.code() <= 299) {
                        mutableLivePostApplyLoan.postValue(response.body());
                    } else if (response.code() >= 400 && response.code() <= 499) {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        apiInfoModel.setMessage(jObjError.getString("message"));
                        apiInfoModel.setStatusCode(response.code());
                        mutableLivePostApplyLoan.postValue(apiInfoModel);
                    } else if (response.code() >= 500) {
                        apiInfoModel.setStatusCode(500);
                        mutableLivePostApplyLoan.postValue(apiInfoModel);
                    }
                } catch (Exception e) {
                    PostLoanApiInfoModel apiInfoModel = new PostLoanApiInfoModel();
                    apiInfoModel.setStatusCode(500);
                    mutableLivePostApplyLoan.postValue(apiInfoModel);
                }
            }

            @Override
            public void onFailure(Call<PostLoanApplyModel> call, Throwable t) {
                PostLoanApiInfoModel apiInfoModel = new PostLoanApiInfoModel();
                apiInfoModel.setStatusCode(500);
                mutableLivePostApplyLoan.postValue(apiInfoModel);
            }
        });
        return mutableLivePostApplyLoan;
    }

    public MutableLiveData<LoanList> getLoanList() {
        Call<LoanList> call = new RetrofitClient().getApiRetrofitInterceptor().getLoanList();
        call.enqueue(new Callback<LoanList>() {
            @Override
            public void onResponse(Call<LoanList> call, Response<LoanList> response) {
                LoanList loanList = new LoanList();
                if (response.code() >= 200 && response.code() <= 299) {
                    mutableGetLoanList.setValue(response.body());
                } else if (response.code() >= 400 && response.code() <= 499) {
                    loanList.setStatusCode(response.code());
                    mutableLivePostApplyLoan.postValue(loanList);
                } else if (response.code() >= 500) {
                    loanList.setStatusCode(500);
                    mutableLivePostApplyLoan.postValue(loanList);
                }
            }

            @Override
            public void onFailure(Call<LoanList> call, Throwable t) {
                LoanList loanList = new LoanList();
                loanList.setStatusCode(500);
                mutableLivePostApplyLoan.postValue(loanList);
            }
        });
        return mutableGetLoanList;
    }

    public LiveData<BannerListDataModel> getDynamicBanner(String companyName, String type) {
        Call<BannerListDataModel> call = new RetrofitClient().getApiRetrofitInterceptor().getBanner(companyName, type);
        call.enqueue(new Callback<BannerListDataModel>() {
            @Override
            public void onResponse(Call<BannerListDataModel> call, Response<BannerListDataModel> response) {
                BannerListDataModel bannerListDataModel = new BannerListDataModel(null, null);
                if (response.code() >= 200 && response.code() <= 299) {
                    mutablebannerData.postValue(response.body());
                } else if (response.code() >= 400 && response.code() <= 499) {
                    bannerListDataModel.setStatusCode(response.code());
                    mutablebannerData.postValue(bannerListDataModel);
                } else if (response.code() >= 500) {
                    bannerListDataModel.setStatusCode(500);
                    mutablebannerData.postValue(bannerListDataModel);
                }
            }

            @Override
            public void onFailure(Call<BannerListDataModel> call, Throwable t) {
                BannerListDataModel bannerListDataModel = new BannerListDataModel(null, null);
                bannerListDataModel.setStatusCode(500);
                mutablebannerData.postValue(bannerListDataModel);
            }
        });
        return mutablebannerData;
    }

    public MutableLiveData<UserResponse> getUserResponse() {
        Call<UserResponse> call = new RetrofitClient().getApiRetrofitInterceptor().getData();
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                try {
                    if (response.code() >= 200 && response.code() <= 299) {
                        mutableUserata.postValue(response.body());
                    } else if (response.code() >= 400 && response.code() <= 499) {
                        mutableUserata.postValue(response.body());
                    } else if (response.code() >= 500) {
                        mutableUserata.postValue(response.body());
                    }

                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {

            }
        });
        return mutableUserata;
    }

    public MutableLiveData<EarnDataModelJava.Documents> getNonPayrollDocs() {
        Call<EarnDataModelJava.Documents> call = new RetrofitClient().getApiRetrofitInterceptor().getNonPayrollDocs();
        call.enqueue(new Callback<EarnDataModelJava.Documents>() {
            @Override
            public void onResponse(Call<EarnDataModelJava.Documents> call, Response<EarnDataModelJava.Documents> response) {
                try {
                    if (response.code() >= 200 && response.code() <= 299) {
                        mutableNonPayrollDocsData.postValue(response.body());
                    } else if (response.code() >= 400 && response.code() <= 499) {
                        mutableNonPayrollDocsData.postValue(response.body());
                    } else if (response.code() >= 500) {
                        mutableNonPayrollDocsData.postValue(response.body());
                    }

                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call<EarnDataModelJava.Documents> call, Throwable t) {

            }
        });
        return mutableNonPayrollDocsData;
    }


    public MutableLiveData<SupportTicketDTO> callFreshDeskAPI(String queryText) {
        Call<SupportTicketDTO> call = new RetrofitClient().freshDeskIntegration().callFreshDeskAPI(queryText);
        call.enqueue(new Callback<SupportTicketDTO>() {
            @Override
            public void onResponse(Call<SupportTicketDTO> call, Response<SupportTicketDTO> response) {
                try {
                    if (response.code() >= 200 && response.code() <= 299) {
                        mutableFreshDeskData.postValue(response.body());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SupportTicketDTO> call, Throwable t) {
                SupportTicketDTO model = new SupportTicketDTO(null);
                mutableFreshDeskData.postValue(model);
            }
        });
        return mutableFreshDeskData;
    }


}
