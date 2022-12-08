package com.vahan.mitra_playstore.models;

import androidx.annotation.Keep;

@Keep
public class RoughDemo {

    private String notification;

    public RoughDemo(String notification) {
        this.notification = notification;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }
}
