/*
 * Copyright 2026 Roozbeh Zarei
 *
 * Licensed under the GNU General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package com.roozbehzarei.filester.service

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.roozbehzarei.filester.domain.service.AnalyticsService

class AnalyticsServiceImpl(
    context: Context,
) : AnalyticsService {
    private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

    override fun setEnabled(enable: Boolean) {
        firebaseAnalytics.setAnalyticsCollectionEnabled(enable)
    }

    override fun logUploadSuccess() {
        firebaseAnalytics.logEvent("file_upload_success", null)
    }

    override fun logUploadFailure() {
        firebaseAnalytics.logEvent("file_upload_failure", null)
    }
}
