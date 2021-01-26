package com.example.chatapp.Utills;

import android.app.NotificationManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.chatapp.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        String title = remoteMessage.getNotification().getTitle();
        String body  = remoteMessage.getNotification().getBody();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),"CHAT");
        builder.setContentTitle(title);
        builder.setContentTitle(body);
        builder.setSmallIcon(R.drawable.logo);
        NotificationManager manager = (NotificationManager)    getSystemService(NOTIFICATION_SERVICE);
        manager.notify(123,builder.build());
    }
}
