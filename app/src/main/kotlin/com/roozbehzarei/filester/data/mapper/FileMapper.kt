/*
 * Copyright 2026 Roozbeh Zarei
 *
 * Licensed under the GNU General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package com.roozbehzarei.filester.data.mapper

import com.roozbehzarei.filester.data.local.FileEntity
import com.roozbehzarei.filester.domain.model.File

fun FileEntity.toFile(): File =
    File(
        id = id,
        name = name,
        downloadUrl = url,
        size = size,
        mimeType = mimeType,
        uploadedAt = uploadedAt,
        expiresAt = expiresAt,
    )

fun File.toFileEntity(): FileEntity =
    FileEntity(
        id = id,
        name = name,
        url = downloadUrl,
        size = size,
        mimeType = mimeType,
        uploadedAt = uploadedAt,
        expiresAt = expiresAt,
    )
