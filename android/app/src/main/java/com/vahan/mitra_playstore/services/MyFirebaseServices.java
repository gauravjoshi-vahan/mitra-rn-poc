//package com.vahan.mitra_android.services;
//
//import android.app.PendingIntent;
//import android.content.Intent;
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//import androidx.room.Room;
//
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//import com.vahan.mitra_android.MainActivity;
//import com.vahan.mitra_android.room.NotificationModel;
//import com.vahan.mitra_android.room.NotificationDao;
//import com.vahan.mitra_android.room.NotificationDatabase;
//
//import java.util.Map;
//
//public class MyFirebaseServices extends FirebaseMessagingService {
//    private static final String DATABASE_NAME = "db_notification";
//    private static int NOTIFICATION_ID = 1;
//    private NotificationDatabase notificationDatabase = null;
//    private NotificationDao dao = null;
//    private String body, title, itemId, type;
//
//    @Override
//    public void onNewToken(@NonNull String s) {
//        super.onNewToken(s);
//    }
//
//    @Override
//    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
//
//        notificationDatabase = Room.databaseBuilder(getApplicationContext(),
//                NotificationDatabase.class, DATABASE_NAME).allowMainThreadQueries().build();
//        dao = notificationDatabase.notificationDao();
//        if (!remoteMessage.getData().isEmpty()) {
//
//            Map<String, String> data = remoteMessage.getData();
//            title = data.get("title");
//            body = data.get("body");
//            itemId = data.get("itemId");
//            type = data.get("type");
//
//            if (notificationDatabase == null) {
//                Log.d("NullCheck", "onMessageReceived:  NULLL");
//            } else {
//                Log.d("NullCheck", "onMessageReceived:  Intialised");
//            }
//            Log.d("NotificationModel", "onMessageReceived: " + title + " " + body + " " + itemId + " " + type);
//
//            Intent intent = new Intent(this, MainActivity.class);
//            if (type.equals("review")) {
//                intent.putExtra("itemId", itemId);
//            }
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//
//            String value = remoteMessage.getData().get("title");
//            String value1 = remoteMessage.getData().get("body");
//            Log.d("NotificationS", "onMessageReceived: " + value + value1);
//            MyNotification myNotificationManager = new MyNotification(this);
//            myNotificationManager.displayNotification(value, value1, new Intent(this,
//                    MainActivity.class));
//
//            new Thread(() -> {
//                Log.d("Thread", "onMessageReceived: " + title + body + itemId);
//                NotificationModel notification = new NotificationModel();
//                notification.setTitle("Hello");
//                notification.setBody("NotificationModel");
//                notification.setId(Integer.parseInt("2"));
//                dao.insertOnlySingleNotification(notification);
//            }).start();
//        }
//    }
//}


package com.vahan.mitra_playstore.services;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.freshchat.consumer.sdk.Freshchat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.moengage.firebase.MoEFireBaseHelper;
import com.moengage.pushbase.MoEPushHelper;
import com.vahan.mitra_playstore.R;
import com.vahan.mitra_playstore.utils.Constants;
import com.vahan.mitra_playstore.view.BaseApplication;

import java.util.Map;

public class MyFirebaseServices extends FirebaseMessagingService {
    private String body, title, type;
    private String itemId;


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (MoEPushHelper.getInstance().isFromMoEngagePlatform(remoteMessage.getData())) {
            Intent intent = new Intent(getResources().getString(R.string.freshchat_file_provider_authority));
            intent.putExtra(Constants.NEW_NOTIFICATION, "received");
            sendBroadcast(intent);
            MoEFireBaseHelper.Companion.getInstance().passPushPayload(getApplicationContext(), remoteMessage.getData());

        }else if (Freshchat.isFreshchatNotification(remoteMessage)) {
            Freshchat.handleFcmMessage(BaseApplication.getContext(), remoteMessage);
            Intent intent = new Intent(Freshchat.FRESHCHAT_UNREAD_MESSAGE_COUNT_CHANGED);
            intent.putExtra(Constants.NEW_NOTIFICATION, "freshchat");
            sendBroadcast(intent);
           /* if (remoteMessage.getData().size() > 0) {
                Map<String, String> data = remoteMessage.getData();
                Log.d("TESTING", "onMessageReceivedData: " + data);
                title = data.get("title");
                body = data.get("body");
                type = data.get("click_action");
                Log.d("NotificationS", "Message data payload: " + remoteMessage.getData());
                Log.d("NotificationModel", "onMessageReceived: " + title + body + itemId + type);
                FreshchatNotificationConfig notificationConfig = new FreshchatNotificationConfig()
                        .setNotificationSoundEnabled(true)    .setSmallIcon(R.drawable.ic_app_icon)    .setLargeIcon(R.drawable.ic_app_icon) .launchActivityOnFinish(MainActivity.class.getName())
                        .setPriority(NotificationCompat.PRIORITY_HIGH);Freshchat.getInstance(getApplicationContext()).setNotificationConfig(notificationConfig);

//                MyNotification myNotificationManager = new MyNotification(this, notificationDatabase);
//                myNotificationManager.displayNotification(title, body, type, new Intent(this,
//                        MainActivity.class));
            }*/
             } else {
            if (remoteMessage.getData().size() > 0) {
                Map<String, String> data = remoteMessage.getData();
                Log.d("TESTING", "onMessageReceivedData: " + data);
                title = data.get(Constants.TITLE);
                body = data.get(Constants.BODY);
                type = data.get(Constants.CLICK_ACTION);
                Log.d("NotificationS", "Message data payload: " + remoteMessage.getData());
                Log.d("NotificationModel", "onMessageReceived: " + title + body + itemId + type);
//                MyNotification myNotificationManager = new MyNotification(this, notificationDatabase);
//                myNotificationManager.displayNotification(title, body, type, new Intent(this,
//                        MainActivity.class));
            }
        }

        if (remoteMessage.getNotification() != null) {
            Log.d("TESTING", "onMessageReceivedNotifi: " + remoteMessage.getNotification().getBody());
            Log.d("TESTING", "onMessageReceivedNotifi: " + remoteMessage.getNotification().getBody());
        }

    }
}

