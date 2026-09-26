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

import com.roozbehzarei.filester.data.repository.FileRepositoryImpl
import com.roozbehzarei.filester.data.repository.UserPreferencesRepositoryImpl
import com.roozbehzarei.filester.domain.repository.FileRepository
import com.roozbehzarei.filester.domain.repository.UserPreferencesRepository
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

val repositoryModule =
    module {
        single<FileRepositoryImpl>() bind FileRepository::class
        single<UserPreferencesRepositoryImpl>() bind UserPreferencesRepository::class
    }
