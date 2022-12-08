package com.vahan.mitra_playstore.models;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Keep
public class TransactionDetailsModelJava {
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("statusCode")
    @Expose
    private int statusCode;
    @SerializedName("transactions")
    @Expose
    private List<Transaction> transactions = null;
    @SerializedName("walletBalance")
    @Expose
    private Double walletBalance;
    @SerializedName("walletBalanceLabel")
    @Expose
    private String walletBalanceLabel;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Double getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(Double walletBalance) {
        this.walletBalance = walletBalance;
    }

    public String getWalletBalanceLabel() {
        return walletBalanceLabel;
    }

    public void setWalletBalanceLabel(String walletBalanceLabel) {
        this.walletBalanceLabel = walletBalanceLabel;
    }

    public static class Transaction {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("amountLabel")
        @Expose
        private String amountLabel;
        @SerializedName("amountInPaisa")
        @Expose
        private Integer amountInPaisa;
        @SerializedName("key")
        @Expose
        private String key;
        @SerializedName("icon")
        @Expose
        private String icon;
        @SerializedName("label")
        @Expose
        private String label;
        @SerializedName("dateTimeStr")
        @Expose
        private String dateTimeStr;
        @SerializedName("dateTime")
        @Expose
        private String dateTime;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("statusLabel")
        @Expose
        private String statusLabel;
        @SerializedName("statusColor")
        @Expose
        private String statusColor;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getAmountLabel() {
            return amountLabel;
        }

        public void setAmountLabel(String amountLabel) {
            this.amountLabel = amountLabel;
        }

        public Integer getAmountInPaisa() {
            return amountInPaisa;
        }

        public void setAmountInPaisa(Integer amountInPaisa) {
            this.amountInPaisa = amountInPaisa;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getDateTimeStr() {
            return dateTimeStr;
        }

        public void setDateTimeStr(String dateTimeStr) {
            this.dateTimeStr = dateTimeStr;
        }

        public String getDateTime() {
            return dateTime;
        }

        public void setDateTime(String dateTime) {
            this.dateTime = dateTime;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStatusLabel() {
            return statusLabel;
        }

        public void setStatusLabel(String statusLabel) {
            this.statusLabel = statusLabel;
        }

        public String getStatusColor() {
            return statusColor;
        }

    }

}

