package com.roozbehzarei.filester.upload

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.ForegroundInfo
import com.roozbehzarei.filester.R
import com.roozbehzarei.filester.framework.FilesterBroadcastReceiver
import com.roozbehzarei.filester.framework.FilesterBroadcastReceiver.Companion.STOP_UPLOAD_ACTION

class UploadNotificationHelper(private val context: Context) {

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

    private val ongoingNotificationBuilder: NotificationCompat.Builder by lazy {
        val cancelIntent = Intent(context, FilesterBroadcastReceiver::class.java).apply {
            action = STOP_UPLOAD_ACTION
        }
        val cancelPendingIntent = PendingIntent.getBroadcast(
            context, -1, cancelIntent, PendingIntent.FLAG_IMMUTABLE
        )
        NotificationCompat.Builder(context, channelId)
            .setContentTitle(context.getString(R.string.notif_title_start))
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(ContextCompat.getColor(context, R.color.seed))
            .setOngoing(true)
            .addAction(0, context.getString(R.string.cancel), cancelPendingIntent)
            .setProgress(100, 0, false)
    }

    fun createForegroundInfo(): ForegroundInfo {
        val notification = ongoingNotificationBuilder.build()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(
                ONGOING_NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(ONGOING_NOTIFICATION_ID, notification)
        }
    }

    fun updateOngoingNotification(@StringRes titleResource: Int, currentProgress: Int) {
        ongoingNotificationBuilder.setContentTitle(context.getString(titleResource))
            .setProgress(100, currentProgress, false)
        notificationManager.notify(ONGOING_NOTIFICATION_ID, ongoingNotificationBuilder.build())
    }

    fun postResultNotification(@StringRes titleResource: Int) {
        val resultNotification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(context.getString(titleResource))
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(ContextCompat.getColor(context, R.color.seed))
            .setOngoing(false)
            .build()
        notificationManager.notify(FINAL_NOTIFICATION_ID, resultNotification)
    }

    fun cancelOngoingNotification() {
        notificationManager.cancel(ONGOING_NOTIFICATION_ID)
    }

    companion object {
        const val ONGOING_NOTIFICATION_ID = 20
        const val FINAL_NOTIFICATION_ID = 10
    }
}