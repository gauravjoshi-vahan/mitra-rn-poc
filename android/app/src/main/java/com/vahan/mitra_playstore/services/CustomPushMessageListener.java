package com.vahan.mitra_playstore.services;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.moengage.pushbase.push.PushMessageListener;
import com.vahan.mitra_playstore.utils.Constants;
import com.vahan.mitra_playstore.utils.PrefrenceUtils;
import com.vahan.mitra_playstore.view.BaseApplication;


import org.jetbrains.annotations.NotNull;

public class CustomPushMessageListener extends PushMessageListener {
    private static final String CHANNEL_ID = "MITRA_ANDROID_random_digits";

    @Override
    public void onNotificationReceived(Context context, Bundle payload) {
        super.onNotificationReceived(context, payload);
        //callback for push notification received.
        Log.d("MoeENgage", "onNonMoEngageMessageReceived: " + payload);
        PrefrenceUtils.insertData(BaseApplication.getContext(), Constants.CLICKED_ACTION, payload.getString("click_action"));
        PrefrenceUtils.insertData(BaseApplication.getContext(), Constants.LINK, payload.getString("link"));
        PrefrenceUtils.insertData(BaseApplication.getContext(), Constants.NOTIFICATION_ID, payload.getString("click_action"));
//        displayNotification(payload.getString("gcm_title"), payload.getString("gcm_alert"), context);
    }

    @Override
    public void onNotificationCleared(@NotNull Context context, @NotNull Bundle payload) {
        super.onNotificationCleared(context, payload);

    }
}
