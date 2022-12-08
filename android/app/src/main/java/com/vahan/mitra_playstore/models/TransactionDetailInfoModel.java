package com.vahan.mitra_playstore.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransactionDetailInfoModel {
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
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("color")
    @Expose
    private String color;
    @SerializedName("info")
    @Expose
    private Info info;
    @SerializedName("transactionMessage")
    @Expose
    private String transactionMessage;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public String getTransactionMessage() {
        return transactionMessage;
    }

    public void setTransactionMessage(String transactionMessage) {
        this.transactionMessage = transactionMessage;
    }

    public static class  Info {

        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("icon")
        @Expose
        private String icon;
        @SerializedName("label")
        @Expose
        private String label;
        @SerializedName("value")
        @Expose
        private Integer value;
        @SerializedName("desc")
        @Expose
        private String desc;
        @SerializedName("message")
        @Expose
        private String message;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
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

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }
}

