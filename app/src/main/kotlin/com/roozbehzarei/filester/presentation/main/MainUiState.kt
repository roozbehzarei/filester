/*
 * Copyright 2026 Roozbeh Zarei
 *
 * Licensed under the GNU General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package com.roozbehzarei.filester.presentation.main

import com.roozbehzarei.filester.domain.model.Theme

data class MainUiState(
    val isLoading: Boolean,
    val isDynamicColor: Boolean,
    val theme: Theme,
)
