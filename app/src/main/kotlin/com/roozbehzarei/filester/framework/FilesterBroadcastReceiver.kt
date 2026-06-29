package com.roozbehzarei.filester.framework

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.WorkManager
import com.roozbehzarei.filester.upload.UploadWorker.Companion.KEY_WORK_NAME

class FilesterBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == STOP_UPLOAD_ACTION) {
            val workManager = context?.let { WorkManager.getInstance(it) }
            workManager?.cancelUniqueWork(KEY_WORK_NAME)
        }
    }

    companion object {
        const val STOP_UPLOAD_ACTION = "FILESTER_STOP_UPLOAD"
    }

}