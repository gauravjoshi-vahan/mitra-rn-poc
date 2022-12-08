package com.vahan.mitra_playstore.models;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class VerificationResponseModel {


    @SerializedName("statusCode")
    @Expose
    private int statusCode;
    @SerializedName("isValid")
    @Expose
    private Boolean isValid;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

}