package com.vahan.mitra_playstore.models;

import android.graphics.drawable.Drawable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OtherSettingModel {
    @SerializedName("other_settings")
    @Expose
    private List<OtherSetting> otherSettings = null;

    public List<OtherSetting> getOtherSettings() {
        return otherSettings;
    }

    public void setOtherSettings(List<OtherSetting> otherSettings) {
        this.otherSettings = otherSettings;
    }

    public static class OtherSetting {

        @SerializedName("url")
        @Expose
        private String url;
        @SerializedName("key")
        @Expose
        private String key;
        @SerializedName("hi_key")
        @Expose
        private String hiKey;
        @SerializedName("icon")
        @Expose
        private int icon;
        @SerializedName("status")
        @Expose
        private String status;

        public String getHiKey() {
            return hiKey;
        }

        public void setHiKey(String hiKey) {
            this.hiKey = hiKey;
        }
        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public int getIcon() {
            return icon;
        }

        public void setIcon(int icon) {
            this.icon = icon;
        }

    }

    @SerializedName("Refer")
    @Expose
    private String refer;
    @SerializedName("FAQ")
    @Expose
    private String faq;
    @SerializedName("Help")
    @Expose
    private String help;
    @SerializedName("Logout")
    @Expose
    private String logout;

    public String getRefer() {
        return refer;
    }

    public void setRefer(String refer) {
        this.refer = refer;
    }

    public String getFaq() {
        return faq;
    }

    public void setFaq(String faq) {
        this.faq = faq;
    }

    public String getHelp() {
        return help;
    }

    public void setHelp(String help) {
        this.help = help;
    }

    public String getLogout() {
        return logout;
    }

    public void setLogout(String logout) {
        this.logout = logout;
    }

}


