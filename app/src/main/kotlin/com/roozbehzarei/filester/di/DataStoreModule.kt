package com.roozbehzarei.filester.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import org.koin.dsl.module
import org.koin.plugin.module.dsl.create

private fun createDataStore(context: Context): DataStore<Preferences> =
    PreferenceDataStoreFactory.create(produceFile = { context.preferencesDataStoreFile("settings") })

val dataStoreModule =
    module {
        single<DataStore<Preferences>> { create(::createDataStore) }
    }
