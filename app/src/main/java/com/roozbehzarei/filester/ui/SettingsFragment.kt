package com.roozbehzarei.filester.ui

import android.app.UiModeManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.roozbehzarei.filester.R


class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val context = preferenceManager.context
        val screen = preferenceManager.createPreferenceScreen(context)
        val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager

        val fileHostingServicePreference = ListPreference(context)
        with(fileHostingServicePreference) {
            key = "file_hosting_service"
            title = getString(R.string.file_hosting_service)
            dialogTitle = getString(R.string.file_hosting_service)
            entries = arrayOf(getString(R.string.link_oshi))
            entryValues = arrayOf(getString(R.string.link_oshi))
            setDefaultValue(getString(R.string.link_oshi))
            summary = getString(R.string.link_oshi)
        }

        val languagePreference = ListPreference(context)
        with(languagePreference) {
            key = "language"
            title = getString(R.string.language)
            dialogTitle = getString(R.string.language)
            entries = arrayOf(getString(R.string.english),getString(R.string.turkish) ,getString(R.string.persian))
            entryValues = arrayOf("en-US","tr" ,"fa-IR")
            setDefaultValue("en-US")
            summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
            onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                val locales = LocaleListCompat.forLanguageTags(newValue as String)
                AppCompatDelegate.setApplicationLocales(locales)
                true
            }
        }

        val themePreference = ListPreference(context)
        with(themePreference) {
            key = "theme"
            title = getString(R.string.theme)
            dialogTitle = getString(R.string.theme)
            entries = arrayOf(
                getString(R.string.system_default),
                getString(R.string.light),
                getString(R.string.dark)
            )
            entryValues = arrayOf("default", "light", "dark")
            setDefaultValue("default")
            summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
            onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    when (newValue) {
                        "default" -> uiModeManager.setApplicationNightMode(UiModeManager.MODE_NIGHT_AUTO)
                        "light" -> uiModeManager.setApplicationNightMode(UiModeManager.MODE_NIGHT_NO)
                        "dark" -> uiModeManager.setApplicationNightMode(UiModeManager.MODE_NIGHT_YES)
                    }
                } else {
                    when (newValue) {
                        "default" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                        "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                }
                true
            }
        }

        with(screen) {
            addPreference(fileHostingServicePreference)
            addPreference(languagePreference)
            addPreference(themePreference)
            preferenceScreen = this
        }
    }
}