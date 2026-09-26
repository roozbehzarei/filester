/*
 * Copyright 2026 Roozbeh Zarei
 *
 * Licensed under the GNU General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package com.roozbehzarei.filester.domain.model

sealed interface UploadResult<out T> {
    /**
     * @param expiresAt when the uploaded file is deleted by the host, as epoch milliseconds.
     * Each host decides this per upload: a fixed retention for some, a function of file size for
     * others.
     */
    data class Success<T>(
        val data: T,
        val expiresAt: Long,
    ) : UploadResult<T>

    data class Loading(
        val progress: Int,
    ) : UploadResult<Nothing>

    data class Error(
        val message: String? = null,
    ) : UploadResult<Nothing>
}
