package com.vahan.mitra_playstore.services;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.work.Data;

import com.vahan.mitra_playstore.R;
import com.vahan.mitra_playstore.utils.Constants;


public class GetAllNotifications extends NotificationListenerService {

    private static final class ApplicationPackageNames {
        public static final String FACEBOOK_PACK_NAME = "com.facebook.katana";
        public static final String FACEBOOK_MESSENGER_PACK_NAME = "com.facebook.orca";
        public static final String WHATSAPP_PACK_NAME = "com.whatsapp";
        public static final String INSTAGRAM_PACK_NAME = "com.instagram.android";

    }

    public static final class InterceptedNotificationCode {
        public static final int FACEBOOK_CODE = 1;
        public static final int WHATSAPP_CODE = 2;
        public static final int INSTAGRAM_CODE = 3;
        public static final int OTHER_NOTIFICATIONS_CODE = 4; // We ignore all notification with code == 4
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String[] notificationCode = matchNotificationCode(sbn);
        Log.d("Notification_code", "onNotificationPosted: " + notificationCode);

        Data data = new Data.Builder().putString(Constants.PACKAGE_NAME, notificationCode[0])
                .putString(Constants.MESSAGE, notificationCode[1]).build();
//        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
//        OneTimeWorkRequest requestInfoSave = new OneTimeWorkRequest.Builder(SendToFireStore.class)
//                .setInputData(data)
//                .setConstraints(constraints)
//                .build();
//        WorkManager.getInstance(BaseApplication.getContext()).enqueue(requestInfoSave);
        Intent intent = new Intent(getPackageName());
        intent.putExtra(Constants.NOTIFICATION_CODE_PACKAGE, notificationCode[0]);
        intent.putExtra(Constants.NOTIFICATION_CODE_MESSAGE, notificationCode[1]);
        sendBroadcast(intent);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // Nothing to do
    }

    private String[] matchNotificationCode(StatusBarNotification sbn) {

        String packageName = sbn.getPackageName();
        Bundle extras = sbn.getNotification().extras;
        if (packageName.equals(ApplicationPackageNames.FACEBOOK_PACK_NAME)
                || packageName.equals(ApplicationPackageNames.FACEBOOK_MESSENGER_PACK_NAME)) {
            return new String[]{sbn.getPackageName(), extras.getString(Notification.EXTRA_TITLE) + getResources().getString(R.string.column) + extras.getString(Notification.EXTRA_TEXT)};
        } else if (packageName.equals(ApplicationPackageNames.INSTAGRAM_PACK_NAME)) {
            return new String[]{sbn.getPackageName(), extras.getString(Notification.EXTRA_TITLE) + getResources().getString(R.string.column) + extras.getString(Notification.EXTRA_TEXT)};
        } else if (packageName.equals(ApplicationPackageNames.WHATSAPP_PACK_NAME)) {
            return new String[]{sbn.getPackageName(), extras.getString(Notification.EXTRA_TITLE) + getResources().getString(R.string.column) + extras.getString(Notification.EXTRA_TEXT)};
        } else {
            return new String[]{sbn.getPackageName(), extras.getString(Notification.EXTRA_TITLE) + getResources().getString(R.string.column) + extras.getString(Notification.EXTRA_TEXT)};
        }
    }


}