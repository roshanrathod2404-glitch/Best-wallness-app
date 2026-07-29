package com.example.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class SmartFitMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("SmartFitFCM", "New FCM Token generated: $token")
        syncTokenToFirestore(token)
    }

    private fun syncTokenToFirestore(token: String) {
        try {
            val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
            val uid = auth.currentUser?.uid
            if (!uid.isNullOrEmpty() && token.isNotEmpty()) {
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .set(
                        mapOf(
                            "fcmToken" to token,
                            "last_token_updated" to System.currentTimeMillis()
                        ),
                        SetOptions.merge()
                    )
            }
        } catch (e: Exception) {
            Log.e("SmartFitFCM", "Failed to sync token to Firestore", e)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("SmartFitFCM", "High-Priority FCM Data Payload received: ${remoteMessage.data}")

        val title = remoteMessage.data["title"]
            ?: remoteMessage.notification?.title
            ?: "SmartFit Live Message"

        val body = remoteMessage.data["body"]
            ?: remoteMessage.notification?.body
            ?: "New message received"

        val senderName = remoteMessage.data["senderName"] ?: ""
        val displayTitle = if (senderName.isNotEmpty()) "$senderName - $title" else title

        showNotification(displayTitle, body)
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "smartfit_live_channel_high_priority"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "SmartFit Instant Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "High-priority alerts for sub-100ms real-time chat messages"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 250, 250)
                setSound(defaultSoundUri, null)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setSound(defaultSoundUri)
            .setVibrate(longArrayOf(0, 250, 250, 250))
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .build()

        notificationManager.notify((System.currentTimeMillis() % 100000).toInt(), notification)
    }
}
