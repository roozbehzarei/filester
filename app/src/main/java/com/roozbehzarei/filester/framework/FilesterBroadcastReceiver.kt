package com.roozbehzarei.filester.framework

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.WorkManager
import com.roozbehzarei.filester.presentation.screens.main.KEY_WORK

class FilesterBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "FILESTER_STOP_UPLOAD") {
            val workManager = context?.let { WorkManager.getInstance(it) }
            workManager?.cancelUniqueWork(KEY_WORK)
        }
    }
}