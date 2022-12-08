package com.vahan.mitra_playstore.models;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class LocalInfoAppInstallModel {
    @SerializedName("appName")
    @Expose
    String appName;
    @SerializedName("updatedAt")
    @Expose
    String updatedAt;
    @SerializedName("firstInstallTime")
    @Expose
    String firstInstallTime;


    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getCreatedOn() {
        return updatedAt;
    }

    public void setCreatedOn(String createdOn) {
        this.updatedAt = createdOn;
    }

    public String getFirstInstallTime() {
        return firstInstallTime;
    }

    public void setFirstInstallTime(String firstInstallTime) {
        this.firstInstallTime = firstInstallTime;
    }

    public LocalInfoAppInstallModel(String appName, String createdOn, String firstInstallTime) {
        this.appName = appName;
        this.updatedAt = createdOn;
        this.firstInstallTime = firstInstallTime;
    }
}
