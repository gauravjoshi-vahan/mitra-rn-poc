package com.vahan.mitra_playstore.models;

import androidx.annotation.Keep;

@Keep
public class StoreInfoModel {
    public String name;
    public String phoneNumber;
    public String createdAt;
    public String userRef;

    public StoreInfoModel(String name, String phoneNumber, String requestType, String createdAt, String userRef) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.requestType = requestType;
        this.createdAt = createdAt;
        this.userRef = userRef;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserRef() {
        return userRef;
    }

    public void setUserRef(String userRef) {
        this.userRef = userRef;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String requestType;


}
