package com.vahan.mitra_playstore.models;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class LocalInfoContactsModel {
    @SerializedName("contactName")
    @Expose
    String contactName;
    @SerializedName("updatedAt")
    @Expose
    String updatedAt;
    @SerializedName("contactNumber")
    @Expose
    String contactNumber;

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }


    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }


    public LocalInfoContactsModel(String contactName, String updatedAt, String contactNumber) {
        this.contactName = contactName;
        this.updatedAt = updatedAt;
        this.contactNumber = contactNumber;
    }
}
