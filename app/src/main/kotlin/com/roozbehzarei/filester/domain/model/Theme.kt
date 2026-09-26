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

sealed class Theme(
    val index: Int,
) {
    data object Light : Theme(0)

    data object Default : Theme(1)

    data object Dark : Theme(2)

    companion object {
        private val entries by lazy { listOf(Light, Default, Dark) }

        fun fromIndexOrDefault(index: Int?): Theme = entries.find { it.index == index } ?: Default
    }
}
