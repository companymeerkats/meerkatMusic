package ir.companymeerkats.myapplication;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class Application extends android.app.Application {
    public static final String channelId1 = "channel1";
    public static final String channelId2 = "channel2";
    public static final String ACTION_NEXT = "NEXT";
    public static final String ACTION_PREV = "PREV";
    public static final String ACTION_PLAY = "PLAY";
    public static final String ACTION_CANCEL = "CANCEL";
    public boolean uiNight;
    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel1 = new NotificationChannel(channelId1, "channel1", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel1.setDescription("channel1");
            NotificationChannel notificationChannel2 = new NotificationChannel(channelId2, "channel2", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel1.setDescription("channel2");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel1);
            notificationManager.createNotificationChannel(notificationChannel2);
        }
    }
}
