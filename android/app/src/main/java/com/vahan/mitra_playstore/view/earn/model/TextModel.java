package com.vahan.mitra_playstore.view.earn.model;

import androidx.annotation.Keep;

@Keep
public class TextModel {
    private String heading;
    private String subHeading;
    private String type;


    public String getHeading() {
        return heading;
    }

    public TextModel(String heading, String subHeading,String type) {
        this.heading = heading;
        this.subHeading = subHeading;
        this.type = type;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getSubHeading() {
        return subHeading;
    }

    public String getType() {
        return type;
    }

    public void setSubHeading(String subHeading) {
        this.subHeading = subHeading;
    }
}
