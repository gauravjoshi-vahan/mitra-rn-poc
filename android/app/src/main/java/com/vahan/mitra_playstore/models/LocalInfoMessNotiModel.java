package com.vahan.mitra_playstore.models;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class LocalInfoMessNotiModel {
    @SerializedName("packageName")
    @Expose
    String packageName;
    @SerializedName("message")
    @Expose
    String message;
    @SerializedName("timeStamp")
    @Expose
    String timeStamp;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public LocalInfoMessNotiModel(String packageName, String message,String timeStamp) {
        this.packageName = packageName;
        this.message = message;
        this.timeStamp = timeStamp;
    }


}