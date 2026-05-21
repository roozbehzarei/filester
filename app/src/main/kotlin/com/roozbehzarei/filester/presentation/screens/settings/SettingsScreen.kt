package com.roozbehzarei.filester.presentation.screens.settings

import android.app.LocaleConfig
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.BrightnessMedium
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Contrast
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.ModeNight
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.os.LocaleListCompat
import com.roozbehzarei.filester.BuildConfig
import com.roozbehzarei.filester.R
import com.roozbehzarei.filester.domain.model.Theme
import com.roozbehzarei.filester.presentation.theme.FilesterAppTheme
import org.koin.compose.koinInject
import java.util.Locale

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier, viewModel: SettingsViewModel = koinInject()
) {
    val context = LocalContext.current
    val appLocales = remember(context) { getApplicationLocales(context) }
    val currentLocale = LocalConfiguration.current.locales.get(0)
    val isDynamicColor by viewModel.getDynamicColorPref.collectAsState(false)
    val theme by viewModel.getThemeModePref.collectAsState(Theme.Default)
    val isTelemetryEnabled by viewModel.getTelemetryPref.collectAsState(false)
    val isCrashReportEnabled by viewModel.getCrashReportPref.collectAsState(true)

    SettingsContent(
        modifier = modifier.verticalScroll(rememberScrollState()),
        appLocales = appLocales,
        currentAppLocale = currentLocale,
        isDynamicColor = isDynamicColor,
        theme = theme,
        isTelemetryEnabled = isTelemetryEnabled,
        isCrashReportEnabled = isCrashReportEnabled,
        onThemeChanged = { viewModel.saveThemeModePref(it) },
        onDynamicColorChanged = { viewModel.saveDynamicColorPref(it) },
        onTelemetryChanged = { viewModel.saveTelemetryPref(it) },
        onCrashReportChanged = { viewModel.saveCrashReportPref(it) })

}

@Composable
private fun SettingsContent(
    modifier: Modifier = Modifier,
    appLocales: List<Locale>,
    currentAppLocale: Locale,
    isDynamicColor: Boolean,
    theme: Theme,
    isTelemetryEnabled: Boolean,
    isCrashReportEnabled: Boolean,
    onThemeChanged: (Theme) -> Unit,
    onDynamicColorChanged: (Boolean) -> Unit,
    onTelemetryChanged: (Boolean) -> Unit,
    onCrashReportChanged: (Boolean) -> Unit
) {

    var shouldShowLanguageDialog by remember { mutableStateOf(false) }

    if (shouldShowLanguageDialog) {
        LanguagePickerDialog(appLocales = appLocales, currentAppLocale = currentAppLocale) {
            shouldShowLanguageDialog = false
        }
    }
    Column(modifier = modifier) {
        SettingsItem(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .defaultMinSize(minHeight = 64.dp),
            title = stringResource(R.string.settings_label_language),
            description = currentAppLocale.displayLanguage,
            icon = Icons.Outlined.Language,
            options = null,
            onClick = { shouldShowLanguageDialog = true },
        )
        SettingsItem(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .defaultMinSize(minHeight = 64.dp),
            title = stringResource(R.string.settings_label_theme),
            description = when (theme) {
                Theme.Light -> stringResource(R.string.settings_label_light)
                Theme.Dark -> stringResource(R.string.settings_label_dark)
                Theme.Default -> stringResource(R.string.settings_label_system_default)
            },
            maxLines = 1,
            icon = Icons.Outlined.BrightnessMedium,
            options = { modifier ->
                val options = listOf(
                    Icons.Outlined.LightMode, Icons.Outlined.Contrast, Icons.Outlined.ModeNight
                )
                SingleChoiceSegmentedButtonRow(modifier = modifier) {
                    options.forEachIndexed { index, icon ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index, count = options.size
                            ),
                            onClick = { onThemeChanged(Theme.fromIndexOrDefault(index)) },
                            selected = index == theme.index,
                            label = { Icon(icon, null) })
                    }
                }
            },
            onClick = null
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            SettingsItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .defaultMinSize(minHeight = 64.dp),
                title = stringResource(R.string.settings_label_dynamic_colors),
                description = stringResource(R.string.settings_description_dynamic_colors),
                icon = Icons.Outlined.Palette,
                options = { modifier ->
                    Switch(
                        modifier = modifier,
                        checked = isDynamicColor,
                        onCheckedChange = { onDynamicColorChanged(it) })
                },
                onClick = null
            )
        }
        if (BuildConfig.FLAVOR == "global") {
            SettingsItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .defaultMinSize(minHeight = 64.dp),
                title = stringResource(R.string.settings_label_telemetry),
                description = stringResource(R.string.settings_description_telemetry),
                icon = Icons.Outlined.Analytics,
                options = { modifier ->
                    Switch(
                        modifier = modifier,
                        checked = isTelemetryEnabled, onCheckedChange = { onTelemetryChanged(it) })
                },
                onClick = null
            )
            SettingsItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .defaultMinSize(minHeight = 64.dp),
                title = stringResource(R.string.settings_label_crash_report),
                description = stringResource(R.string.settings_description_crash_report),
                icon = Icons.Outlined.BugReport,
                options = { modifier ->
                    Switch(
                        modifier = modifier,
                        checked = isCrashReportEnabled,
                        onCheckedChange = { onCrashReportChanged(it) })
                },
                onClick = null
            )
        }
    }
}

@Composable
private fun SettingsItem(
    modifier: Modifier = Modifier,
    title: String,
    description: String, maxLines: Int = Int.MAX_VALUE,
    icon: ImageVector, options: (@Composable (modifier: Modifier) -> Unit)?,
    onClick: (() -> Unit)?
) {
    val finalModifier = if (onClick != null) {
        modifier.clickable {
            onClick()
        }
    } else {
        modifier
    }
    Row(
        modifier = finalModifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.padding(horizontal = 18.dp),
            imageVector = icon,
            contentDescription = null
        )
        Column(Modifier.weight(1f)) {
            Text(
                title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                description, maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall
            )
        }
        if (options != null) options(Modifier.padding(horizontal = 18.dp))
    }
}

@Composable
private fun LanguagePickerDialog(
    appLocales: List<Locale>, currentAppLocale: Locale, onDismissRequest: () -> Unit
) {

    var selectedOption by remember { mutableStateOf(currentAppLocale) }

    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)
        ) {
            Column {
                Text(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = stringResource(R.string.settings_label_language),
                    maxLines = 1,
                    style = MaterialTheme.typography.titleLarge
                )
                appLocales.forEach { locale ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = selectedOption == locale, onClick = {
                                    selectedOption = locale
                                }, role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            modifier = Modifier.padding(12.dp),
                            selected = selectedOption == locale,
                            onClick = null,
                        )
                        Text(
                            modifier = Modifier, text = locale.getDisplayName(locale),
                            maxLines = 1,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                }
                Row(Modifier.padding(16.dp)) {
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = {
                        onDismissRequest()
                    }) {
                        Text(stringResource(R.string.cancel))
                    }
                    TextButton(onClick = {
                        val locateListCompat = LocaleListCompat.create(selectedOption)
                        AppCompatDelegate.setApplicationLocales(locateListCompat)
                        onDismissRequest()
                    }) {
                        Text(stringResource(R.string.apply))
                    }
                }
            }
        }
    }
}

private fun getApplicationLocales(context: Context): List<Locale> {
    val locales = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        LocaleConfig(context).supportedLocales ?: LocaleList.getEmptyLocaleList()
    } else {
        val englishLocale = Locale.Builder().setLocale(Locale.ENGLISH).build()
        val persianLocale = Locale.Builder().setLanguageTag("fa-rIR").build()
        val turkishLocale = Locale.Builder().setLanguageTag("tr").build()
        LocaleList(englishLocale, persianLocale, turkishLocale)
    }
    return (0 until locales.size()).map { locales.get(it) }
        .sortedBy { locale -> locale.getDisplayName((locale)) }
}

@Preview(device = Devices.PHONE)
@Preview(device = Devices.FOLDABLE)
@Preview(device = Devices.TABLET)
@Composable
private fun SettingsContentPreview() {
    FilesterAppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            SettingsContent(
                modifier = Modifier.fillMaxSize(),
                appLocales = emptyList(),
                currentAppLocale = LocalConfiguration.current.locales.get(0),
                isDynamicColor = false,
                theme = Theme.Default,
                isTelemetryEnabled = false,
                isCrashReportEnabled = false,
                onThemeChanged = {},
                onDynamicColorChanged = {},
                onTelemetryChanged = {},
                onCrashReportChanged = {})
        }
    }
}

@Preview
@Composable
private fun LanguagePickerDialogPreview() {

    val locale1 = Locale.Builder().setLocale(Locale.ENGLISH).build()
    val locale2 = Locale.Builder().setLocale(Locale.FRENCH).build()
    val locale3 = Locale.Builder().setLocale(Locale.JAPANESE).build()
    val dummyLocaleList = listOf(locale1, locale2, locale3)

    FilesterAppTheme {
        Surface {
            LanguagePickerDialog(
                appLocales = dummyLocaleList, currentAppLocale = Locale.ENGLISH
            ) {}
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsItemPreview() {
    FilesterAppTheme {
        Surface {
            SettingsItem(
                modifier = Modifier.padding(vertical = 18.dp),
                title = stringResource(R.string.settings_label_theme),
                description = stringResource(R.string.settings_label_system_default),
                icon = Icons.Outlined.BrightnessMedium,
                options = {
                    Switch(
                        checked = true, onCheckedChange = { })
                },
                onClick = null
            )
        }
    }
}