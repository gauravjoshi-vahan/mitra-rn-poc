package com.vahan.mitra_playstore.models;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class LocaInfoSMSModel {
    @SerializedName("text")
    @Expose
    String text;
    @SerializedName("updateAt")
    @Expose
    String updateAt;
    @SerializedName("senderAddress")
    @Expose
    String senderAddress;

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }



    @SerializedName("receivedAt")
    @Expose
    String receivedAt;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(String receivedAt) {
        this.receivedAt = receivedAt;
    }

    public LocaInfoSMSModel(String senderAddress, String updateAt, String text, String receivedAt) {
        this.senderAddress = senderAddress;
        this.updateAt = updateAt;
        this.text = text;
        this.receivedAt = receivedAt;
    }
}
