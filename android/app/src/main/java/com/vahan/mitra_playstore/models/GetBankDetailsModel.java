package com.vahan.mitra_playstore.models;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class GetBankDetailsModel {

    @SerializedName("statusCode")
    @Expose
    private int statusCode;
    @SerializedName("bankAccountDetails")
    @Expose
    private BankAccountDetails bankAccountDetails;
    @SerializedName("isBankAccountVerificationInProgress")
    @Expose
    private boolean isBankAccountVerificationInProgress ;

    public boolean isBankAccountVerificationInProgress() {
        return isBankAccountVerificationInProgress;
    }

    public void setBankAccountVerificationInProgress(boolean bankAccountVerificationInProgress) {
        isBankAccountVerificationInProgress = bankAccountVerificationInProgress;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @SerializedName("message")
    @Expose
    private String message ;

    public BankAccountDetails getBankAccountDetails() {
        return bankAccountDetails;
    }

    public void setBankAccountDetails(BankAccountDetails bankAccountDetails) {
        this.bankAccountDetails = bankAccountDetails;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public static class BankAccountDetails {

        @SerializedName("accountNumber")
        @Expose
        private String accountNumber;
        @SerializedName("ifsc")
        @Expose
        private String ifsc;
        @SerializedName("accountName")
        @Expose
        private String accountName;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("bankName")
        @Expose
        private String bankName;
        @SerializedName("bankLogo")
        @Expose
        private String bankLogo;

        @SerializedName("accountType")
        @Expose
        private String accountType;

        public String getAccountType() {
            return accountType;
        }

        public void setAccountType(String accountType) {
            this.accountType = accountType;
        }


        public String getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        public String getIfsc() {
            return ifsc;
        }

        public void setIfsc(String ifsc) {
            this.ifsc = ifsc;
        }

        public String getAccountName() {
            return accountName;
        }

        public void setAccountName(String accountName) {
            this.accountName = accountName;
        }

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }

        public String getBankLogo() {
            return bankLogo;
        }

        public void setBankLogo(String bankLogo) {
            this.bankLogo = bankLogo;
        }
        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

    }

}

