package com.vahan.mitra_playstore.services;

public class MyNotification {

//    private static final String CHANNEL_ID = "MITRA_ANDROID_random_digits";
//    private static final String DATABASE_NAME = "db_notification";
//    private NotificationDatabase notificationDatabase;
//    Context ctx;
//
//
//    public MyNotification(Context ctx, NotificationDatabase notificationDatabase) {
//        this.ctx = ctx;
//        this.notificationDatabase = notificationDatabase;
//    }
//
//    public void displayNotification(String title, String body, String type, Intent intent) {
//        int NOTIFICATION_ID = 1;
//        Bitmap bitmap_image = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_app_icon);
//        Bitmap big_bitmap_image = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.ic_app_icon);
//        NotificationCompat.BigPictureStyle style = new NotificationCompat.BigPictureStyle()
//                .bigPicture(big_bitmap_image);
//        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            CharSequence name = "my_channel";
//            String Description = "This is my channel";
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
//            mChannel.setDescription(Description);
//            mChannel.setLightColor(Color.RED);
//            mChannel.enableVibration(true);
//            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
//            mChannel.setShowBadge(false);
//            notificationManager.createNotificationChannel(mChannel);
//            @SuppressLint("ResourceAsColor")
//            Notification.Builder builder = new Notification.Builder(ctx, CHANNEL_ID)
//                    .setSmallIcon(R.drawable.dialog_icon)
//                    .setContentTitle(title)
//                    .setContentText(body)
//                    .setLargeIcon(bitmap_image)
//                    .setAutoCancel(true)
//                    .setColorized(true)
//                    .setLargeIcon(bitmap_image)
//                    .setColor(ctx.getColor(R.color.purple_200));
//            PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            builder.setContentIntent(pendingIntent);
//            if (NOTIFICATION_ID > 1073741824) {
//                NOTIFICATION_ID = 0;
//            }
//            Objects.requireNonNull(notificationManager).notify(NOTIFICATION_ID++, builder.build());
//            new Thread(() -> {
//                NotificationModel notification = new NotificationModel();
//                notification.setTitle(title);
//                notification.setBody(body);
//                notification.setType(type);
//                notificationDatabase.notificationDao().insertOnlySingleNotification(notification);
//                Log.d("InsertNotification", "displayNotification: " + notificationDatabase.notificationDao().size());
//            }).start();
//        } else {
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx)
//                    .setSmallIcon(R.drawable.dialog_icon)
//                    .setContentTitle(title)
//                    .setContentText(body).setAutoCancel(true);
//            PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            builder.setContentIntent(pendingIntent);
//            notificationManager.notify(NOTIFICATION_ID, builder.build());
//        }
//    }


}
