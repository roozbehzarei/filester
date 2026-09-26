/*
 * Copyright 2026 Roozbeh Zarei
 *
 * Licensed under the GNU General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gnu.org/licenses/gpl-3.0.en.html
 */

package com.roozbehzarei.filester.di

import com.roozbehzarei.filester.domain.service.AnalyticsService
import com.roozbehzarei.filester.service.AnalyticsServiceImpl
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

val serviceModule =
    module {
        single<AnalyticsServiceImpl>() bind AnalyticsService::class
    }
