package com.creaginetech.xpreshoesshipper.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.creaginetech.xpreshoesshipper.Common.Common;
import com.creaginetech.xpreshoesshipper.Helper.NotificationHelper;
import com.creaginetech.xpreshoesshipper.HomeActivity;
import com.creaginetech.xpreshoesshipper.MainActivity;
import com.creaginetech.xpreshoesshipper.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            sendNotificationAPI26(remoteMessage);
        else
            sendNotification(remoteMessage);
    }

    private void sendNotificationAPI26(RemoteMessage remoteMessage) {
//        RemoteMessage.Notification notification = remoteMessage.getNotification();
//        String title = notification.getTitle();
//        String content = notification.getBody();

        Map<String,String> notification = remoteMessage.getData();
        String title = notification.get("title");
        String content = notification.get("message");

        PendingIntent pendingIntent;
        NotificationHelper helper;
        Notification.Builder builder;

        //Here we will fix to click to notification -> go to Order list
        if(Common.currentShipper != null) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            helper = new NotificationHelper(this);
            builder = helper.getExpreshoesChannelNotification(title, content, pendingIntent, defaultSoundUri);

            //Gen random Id for notification to show all notification
            helper.getManager().notify(new Random().nextInt(), builder.build());
        } else {
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            helper = new NotificationHelper(this);
            builder = helper.getExpreshoesChannelNotification(title,content,defaultSoundUri);

            //Gen random Id for notification to show all notification
            helper.getManager().notify(new Random().nextInt(),builder.build());

        }
    }

    private void sendNotification(RemoteMessage remoteMessage) {
//        RemoteMessage.Notification notification = remoteMessage.getNotification();

        Map<String, String> notification = remoteMessage.getData();
        String title = notification.get("title");
        String content = notification.get("message");

        if (Common.currentShipper != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_local_shipping_black_24dp)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, builder.build());
        } else {
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_local_shipping_black_24dp)
                    .setContentTitle(title)
                    .setContentText(content)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, builder.build());
        }
    }
}
