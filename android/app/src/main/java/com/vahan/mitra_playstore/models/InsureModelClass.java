package com.vahan.mitra_playstore.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InsureModelClass {

    @SerializedName("EC")
    @Expose
    private Ec ec;
    @SerializedName("EW")
    @Expose
    private Ew ew;
    @SerializedName("NE")
    @Expose
    private Ne ne;
    @SerializedName("E")
    @Expose
    private E e;

    public Ec getEc() {
        return ec;
    }

    public void setEc(Ec ec) {
        this.ec = ec;
    }

    public Ew getEw() {
        return ew;
    }

    public void setEw(Ew ew) {
        this.ew = ew;
    }

    public Ne getNe() {
        return ne;
    }

    public void setNe(Ne ne) {
        this.ne = ne;
    }

    public E getE() {
        return e;
    }

    public void setE(E e) {
        this.e = e;
    }

    public static class Ne {

        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("desc")
        @Expose
        private String desc;
        @SerializedName("desc_hi")
        @Expose
        private String descHi;
        @SerializedName("button")
        @Expose
        private Integer button;
        @SerializedName("button_text")
        @Expose
        private String buttonText;
        @SerializedName("button_text_hi")
        @Expose
        private String buttonTextHi;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getDescHi() {
            return descHi;
        }

        public void setDescHi(String descHi) {
            this.descHi = descHi;
        }

        public Integer getButton() {
            return button;
        }

        public void setButton(Integer button) {
            this.button = button;
        }

        public String getButtonText() {
            return buttonText;
        }

        public void setButtonText(String buttonText) {
            this.buttonText = buttonText;
        }

        public String getButtonTextHi() {
            return buttonTextHi;
        }

        public void setButtonTextHi(String buttonTextHi) {
            this.buttonTextHi = buttonTextHi;
        }

    }

    public static class Ec {

        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("title_hi")
        @Expose
        private String titleHi;
        @SerializedName("desc")
        @Expose
        private String desc;
        @SerializedName("hi_desc")
        @Expose
        private String hiDesc;
        @SerializedName("button")
        @Expose
        private Integer button;
        @SerializedName("button_text")
        @Expose
        private String buttonText;
        @SerializedName("button_text_hi")
        @Expose
        private String buttonTextHi;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitleHi() {
            return titleHi;
        }

        public void setTitleHi(String titleHi) {
            this.titleHi = titleHi;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getHiDesc() {
            return hiDesc;
        }

        public void setHiDesc(String hiDesc) {
            this.hiDesc = hiDesc;
        }

        public Integer getButton() {
            return button;
        }

        public void setButton(Integer button) {
            this.button = button;
        }

        public String getButtonText() {
            return buttonText;
        }

        public void setButtonText(String buttonText) {
            this.buttonText = buttonText;
        }

        public String getButtonTextHi() {
            return buttonTextHi;
        }

        public void setButtonTextHi(String buttonTextHi) {
            this.buttonTextHi = buttonTextHi;
        }

    }

    public static class Ew {

        @SerializedName("title")
        @Expose
        private String title;
        @SerializedName("title_hi")
        @Expose
        private String titleHi;
        @SerializedName("hi_desc")
        @Expose
        private String hiDesc;
        @SerializedName("desc")
        @Expose
        private String desc;
        @SerializedName("desc_hi")
        @Expose
        private String descHi;
        @SerializedName("button")
        @Expose
        private Integer button;
        @SerializedName("button_text")
        @Expose
        private String buttonText;
        @SerializedName("button_text_hi")
        @Expose
        private String buttonTextHi;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitleHi() {
            return titleHi;
        }

        public void setTitleHi(String titleHi) {
            this.titleHi = titleHi;
        }

        public String getHiDesc() {
            return hiDesc;
        }

        public void setHiDesc(String hiDesc) {
            this.hiDesc = hiDesc;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getDescHi() {
            return descHi;
        }

        public void setDescHi(String descHi) {
            this.descHi = descHi;
        }

        public Integer getButton() {
            return button;
        }

        public void setButton(Integer button) {
            this.button = button;
        }

        public String getButtonText() {
            return buttonText;
        }

        public void setButtonText(String buttonText) {
            this.buttonText = buttonText;
        }

        public String getButtonTextHi() {
            return buttonTextHi;
        }

        public void setButtonTextHi(String buttonTextHi) {
            this.buttonTextHi = buttonTextHi;
        }

    }

    public static class E {

        @SerializedName("view_details_url")
        @Expose
        private String viewDetailsUrl;
        @SerializedName("claim_desc")
        @Expose
        private String claimDesc;
        @SerializedName("claim_desc_hi")
        @Expose
        private String claimDescHi;
        @SerializedName("help_desc")
        @Expose
        private String helpDesc;
        @SerializedName("help_desc_hi")
        @Expose
        private String helpDescHi;
        @SerializedName("claim_button")
        @Expose
        private Integer claimButton;
        @SerializedName("claim_phone")
        @Expose
        private String claimPhone;
        @SerializedName("help_button")
        @Expose
        private Integer helpButton;
        @SerializedName("help_phone")
        @Expose
        private String helpPhone;

        public String getViewDetailsUrl() {
            return viewDetailsUrl;
        }

        public void setViewDetailsUrl(String viewDetailsUrl) {
            this.viewDetailsUrl = viewDetailsUrl;
        }

        public String getClaimDesc() {
            return claimDesc;
        }

        public void setClaimDesc(String claimDesc) {
            this.claimDesc = claimDesc;
        }

        public String getClaimDescHi() {
            return claimDescHi;
        }

        public void setClaimDescHi(String claimDescHi) {
            this.claimDescHi = claimDescHi;
        }

        public String getHelpDesc() {
            return helpDesc;
        }

        public void setHelpDesc(String helpDesc) {
            this.helpDesc = helpDesc;
        }

        public String getHelpDescHi() {
            return helpDescHi;
        }

        public void setHelpDescHi(String helpDescHi) {
            this.helpDescHi = helpDescHi;
        }

        public Integer getClaimButton() {
            return claimButton;
        }

        public void setClaimButton(Integer claimButton) {
            this.claimButton = claimButton;
        }

        public String getClaimPhone() {
            return claimPhone;
        }

        public void setClaimPhone(String claimPhone) {
            this.claimPhone = claimPhone;
        }

        public Integer getHelpButton() {
            return helpButton;
        }

        public void setHelpButton(Integer helpButton) {
            this.helpButton = helpButton;
        }

        public String getHelpPhone() {
            return helpPhone;
        }

        public void setHelpPhone(String helpPhone) {
            this.helpPhone = helpPhone;
        }

    }

}



