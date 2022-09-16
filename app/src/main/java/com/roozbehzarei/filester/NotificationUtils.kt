package com.roozbehzarei.filester

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


const val CHANNEL_ID = "FILESTER"

fun showNotification(
    context: Context,
    notificationId: Int,
    title: String,
    description: String,
    showProgress: Boolean
) {
    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_upload)
        .setContentTitle(title)
        .setContentText(description)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
    if (showProgress) {
        notification.setProgress(0, 0, true)
    }
    with(NotificationManagerCompat.from(context)) {
        notify(notificationId, notification.build())
    }

}