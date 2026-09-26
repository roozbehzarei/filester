/*
 * Copyright 2026 Roozbeh Zarei
 *
 * Licensed under the GNU General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package com.roozbehzarei.filester.data.network.uguu

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UguuResponse(
    val success: Boolean,
    val files: List<UguuFileItem>? = null,
)

@Serializable
data class UguuFileItem(
    @SerialName("hash") val hash: String? = null,
    @SerialName("filename") val name: String? = null,
    @SerialName("url") val url: String,
)
