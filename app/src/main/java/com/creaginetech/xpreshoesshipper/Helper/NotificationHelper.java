package com.creaginetech.xpreshoesshipper.Helper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import com.creaginetech.xpreshoesshipper.R;

public class NotificationHelper extends ContextWrapper {

    private static final String expreshoes_CHANEL_ID = "com.creaginetech.xpreshoesshipper";
    private static final String expreshoes_CHANEL_Name = "Expreshoes";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) // Only working this function if API is 26 or higher
            createChannel();

    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel expreshoesChannel = new NotificationChannel(expreshoes_CHANEL_ID, expreshoes_CHANEL_Name, NotificationManager.IMPORTANCE_DEFAULT);
        expreshoesChannel.enableLights(false);
        expreshoesChannel.enableVibration(true);
        expreshoesChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(expreshoesChannel);
    }

    public NotificationManager getManager() {
        if (manager == null)
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public android.app.Notification.Builder getExpreshoesChannelNotification(String title, String body, PendingIntent contentIntent, Uri soundUri) {
        return new android.app.Notification.Builder(getApplicationContext(), expreshoes_CHANEL_ID)
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_local_shipping_black_24dp)
                .setSound(soundUri)
                .setAutoCancel(false);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public android.app.Notification.Builder getExpreshoesChannelNotification(String title, String body, Uri soundUri) {
        return new android.app.Notification.Builder(getApplicationContext(), expreshoes_CHANEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_local_shipping_black_24dp)
                .setSound(soundUri)
                .setAutoCancel(false);
    }
}