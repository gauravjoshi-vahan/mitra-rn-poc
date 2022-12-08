package com.vahan.mitra_playstore.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InsuranceModel {
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("statusCode")
    @Expose
    private int statusCode;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("premiumInfo")
    @Expose
    private List<PremiumInfo> premiumInfo = null;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<PremiumInfo> getPremiumInfo() {
        return premiumInfo;
    }

    public void setPremiumInfo(List<PremiumInfo> premiumInfo) {
        this.premiumInfo = premiumInfo;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public static class PremiumInfo {

        @SerializedName("premiumId")
        @Expose
        private String premiumId;
        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("premiumRate")
        @Expose
        private String premiumRate;
        @SerializedName("sumAssured")
        @Expose
        private String sumAssured;
        @SerializedName("dueFrom")
        @Expose
        private String dueFrom;
        @SerializedName("expiresOn")
        @Expose
        private String expiresOn;
        @SerializedName("policyStatus")
        @Expose
        private String policyStatus;
        @SerializedName("insuranceProvider")
        @Expose
        private String insuranceProvider;
        @SerializedName("insuranceName")
        @Expose
        private String insuranceName;
        @SerializedName("insuranceProviderLogo")
        @Expose
        private String insuranceProviderLogo;
        @SerializedName("contactNumber")
        @Expose
        private String contactNumber;
        @SerializedName("durationTime")
        @Expose
        private String durationTime;
        @SerializedName("durationDay")
        @Expose
        private String durationDay;

        public String getPremiumId() {
            return premiumId;
        }

        public void setPremiumId(String premiumId) {
            this.premiumId = premiumId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getPremiumRate() {
            return premiumRate;
        }

        public void setPremiumRate(String premiumRate) {
            this.premiumRate = premiumRate;
        }

        public String getSumAssured() {
            return sumAssured;
        }

        public void setSumAssured(String sumAssured) {
            this.sumAssured = sumAssured;
        }

        public String getDueFrom() {
            return dueFrom;
        }

        public void setDueFrom(String dueFrom) {
            this.dueFrom = dueFrom;
        }

        public String getExpiresOn() {
            return expiresOn;
        }

        public void setExpiresOn(String expiresOn) {
            this.expiresOn = expiresOn;
        }

        public String getPolicyStatus() {
            return policyStatus;
        }

        public void setPolicyStatus(String policyStatus) {
            this.policyStatus = policyStatus;
        }

        public String getInsuranceProvider() {
            return insuranceProvider;
        }

        public void setInsuranceProvider(String insuranceProvider) {
            this.insuranceProvider = insuranceProvider;
        }

        public String getInsuranceName() {
            return insuranceName;
        }

        public void setInsuranceName(String insuranceName) {
            this.insuranceName = insuranceName;
        }

        public String getInsuranceProviderLogo() {
            return insuranceProviderLogo;
        }

        public void setInsuranceProviderLogo(String insuranceProviderLogo) {
            this.insuranceProviderLogo = insuranceProviderLogo;
        }

        public String getContactNumber() {
            return contactNumber;
        }

        public void setContactNumber(String contactNumber) {
            this.contactNumber = contactNumber;
        }

        public String getDurationTime() {
            return durationTime;
        }

        public void setDurationTime(String durationTime) {
            this.durationTime = durationTime;
        }

        public String getDurationDay() {
            return durationDay;
        }

        public void setDurationDay(String durationDay) {
            this.durationDay = durationDay;
        }

    }

}



