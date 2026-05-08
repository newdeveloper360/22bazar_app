package com.userplay.bazar22.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.userplay.bazar22.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.Objects


class MyFirebaseMessagingService : FirebaseMessagingService() {


    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (!Objects.equals(null, message.getNotification())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel(
                    "420",
                    "firebasse_channel",
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(notificationChannel)
            }
           /* val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            val customView = RemoteViews(this.packageName, R.layout.custom_notification).apply {
                setTextViewText(R.id.customNotificationTitle, message.getNotification()?.getTitle())
                setTextViewText(R.id.customNotificationText, message.getNotification()?.getBody())
            }*/
            val notificationBuilder: NotificationCompat.Builder =
                NotificationCompat.Builder(this, "420")
            notificationBuilder.setAutoCancel(true)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(message.getNotification()?.getBody())
                )
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.favicon)
                .setTicker(message.getNotification()?.getTitle())
                .setPriority(Notification.PRIORITY_MAX)
                .setContentTitle(message.getNotification()?.getTitle())
                .setContentText(message.getNotification()?.getBody())
            notificationManager.notify(1, notificationBuilder.build())
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("firebase_refresh_token", "Refreshed token: $token")

    }
}