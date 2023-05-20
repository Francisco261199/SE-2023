package com.se.ding.network;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.se.ding.R;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "ding_default_channel";
    private static final String ACTION_NOTIFICATION_RECEIVED = "com.se.app.NOTIFICATION_RECEIVED";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle the incoming message
        if (remoteMessage.getData().size() > 0) {
            // Extract the notification data
            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            Log.d("FCM", "Received message: " + body);

            // Check if the app is running in the foreground
            if (isAppRunning()) {
                // Broadcast a message to the current activity
                sendNotificationReceivedBroadcast(title, body);
            } else {
                // Create and show a notification
                createNotificationChannel();
                showNotification(title, body);
            }
        }
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        // Send the token to your server for identification or further processing
        // You can store the token in SharedPreferences or send it to your server
        Log.d("FCM", "New token: " + token);
    }

    private boolean isAppRunning() {
        // Implement your logic to check if the app is running in the foreground
        // For example, you can use a shared preference or check the activity stack
        // and return true if the app is currently active
        // Otherwise, return false
        return false;
    }

    private void sendNotificationReceivedBroadcast(String title, String body) {
        Intent intent = new Intent(ACTION_NOTIFICATION_RECEIVED);
        intent.putExtra("title", title);
        intent.putExtra("body", body);
        sendBroadcast(intent);
    }

    private void createNotificationChannel() {
        // Create a notification channel for devices running Android Oreo (API 26) and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "My Channel";
            String channelDescription = "Channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            channel.setDescription(channelDescription);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification(String title, String body) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.camera)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, builder.build());
    }
}
