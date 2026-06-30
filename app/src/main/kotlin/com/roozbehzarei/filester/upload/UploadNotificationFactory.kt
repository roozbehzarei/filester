package com.roozbehzarei.filester.upload

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.roozbehzarei.filester.R

class UploadNotificationFactory(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val channelId = context.getString(R.string.notification_channel_id)

    init {
        createChannel()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notif_label_channel_name)
            val descriptionText = context.getString(R.string.notif_description_channel)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createOngoing(title: String, text: String, progress: Int, cancelIntent: PendingIntent) =
        NotificationCompat.Builder(context, channelId).setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title).setContentText(text).setOngoing(true)
            .setProgress(100, progress, false).addAction(
                R.drawable.ic_outlined_cancel, context.getString(R.string.cancel), cancelIntent
            ).build()

    fun createResultAndNotify(id: Int, title: String, text: String) {
        val notification =
            NotificationCompat.Builder(context, channelId).setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title).setContentText(text).setProgress(0, 0, false)
                .setAutoCancel(true).clearActions().build()
        notificationManager.notify(id, notification)
    }

}