package com.roozbehzarei.filester.ui.screens.settings

import android.os.Build
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BrightnessMedium
import androidx.compose.material.icons.outlined.Contrast
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.ModeNight
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.SystemUpdate
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.os.LocaleListCompat
import com.roozbehzarei.filester.BuildConfig
import com.roozbehzarei.filester.R
import com.roozbehzarei.filester.data.local.UserPreferencesRepository
import com.roozbehzarei.filester.ui.SharedViewModel
import com.roozbehzarei.filester.ui.screens.about.launchUrl
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import java.util.Locale

private val localeOptions = mapOf(
    R.string.english to "en", R.string.turkish to "tr", R.string.persian to "fa"
)

@Composable
fun SettingsScreen(
    userPreferencesRepository: UserPreferencesRepository = koinInject(),
    viewModel: SharedViewModel = koinInject()
) {
    val uiState by viewModel.settingsUiState.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val currentLocale = LocalConfiguration.current.locales.get(0)
    var shouldShowLanguageDialog by remember { mutableStateOf(false) }
    val isDynamicColor by userPreferencesRepository.getDynamicColorsPreference.collectAsState(false)
    val themeModeIndex by userPreferencesRepository.getThemePreference.collectAsState(1)
    if (shouldShowLanguageDialog) {
        LanguagePickerDialog {
            shouldShowLanguageDialog = false
        }
    }
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        SettingsItem(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .defaultMinSize(minHeight = 64.dp),
            title = stringResource(R.string.language),
            description = currentLocale.displayLanguage,
            icon = Icons.Outlined.Language,
            options = null,
            onClick = {
                shouldShowLanguageDialog = true
            },
        )
        SettingsItem(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .defaultMinSize(minHeight = 64.dp),
            title = stringResource(R.string.theme),
            description = when (themeModeIndex) {
                0 -> stringResource(R.string.light)
                2 -> stringResource(R.string.dark)
                else -> stringResource(R.string.system_default)
            },
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
                            ), onClick = {
                                scope.launch {
                                    userPreferencesRepository.saveThemeModePreference(index)
                                }
                            }, selected = index == themeModeIndex, label = { Icon(icon, null) })
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
                title = "Dynamic colors",
                description = "Adapt colors to match your device's wallpaper automatically",
                icon = Icons.Outlined.Palette,
                options = { modifier ->
                    Switch(
                        modifier = modifier, checked = isDynamicColor, onCheckedChange = {
                            scope.launch {
                                userPreferencesRepository.saveDynamicColorsPreference(it)
                            }
                        })
                },
                onClick = null
            )
        }
        uiState.appConfig?.let { appConfig ->
            if (BuildConfig.VERSION_CODE < appConfig.versionCode) {
                SettingsItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .defaultMinSize(minHeight = 64.dp),
                    title = "Update Available",
                    description = "A new version of the app is available. Update now to get the best experience.",
                    icon = Icons.Outlined.SystemUpdate,
                    options = null,
                    onClick = {
                        val isLaunched =
                            launchUrl(context = context, url = uiState.appConfig!!.downloadUrl)
                        if (isLaunched.not()) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.toast_app_not_found),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            }
        }
    }
}

@Composable
private fun SettingsItem(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    icon: ImageVector,
    options: (@Composable (modifier2: Modifier) -> Unit)?,
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
                description,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        if (options != null) options(Modifier.padding(horizontal = 18.dp))
    }
}

@Composable
private fun LanguagePickerDialog(onDismissRequest: () -> Unit) {

    val currentLocale = Locale.getDefault().language
    var selectedLocale by remember { mutableStateOf(currentLocale) }

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
                    text = stringResource(R.string.language),
                    maxLines = 1,
                    style = MaterialTheme.typography.titleLarge
                )
                localeOptions.forEach { locale ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = selectedLocale == locale.value, onClick = {
                                    selectedLocale = locale.value
                                }, role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedLocale == locale.value,
                            onClick = {
                                selectedLocale = locale.value
                            },
                        )
                        Text(
                            modifier = Modifier,
                            text = stringResource(locale.key),
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
                        Text(stringResource(R.string.button_cancel))
                    }
                    TextButton(onClick = {
                        val appLocale = LocaleListCompat.forLanguageTags(selectedLocale)
                        AppCompatDelegate.setApplicationLocales(appLocale)
                    }) {
                        Text(stringResource(R.string.button_apply))
                    }
                }
            }


        }
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    SettingsScreen()
}

@Preview
@Composable
private fun SettingsItemPreview() {
    SettingsItem(
        modifier = Modifier.padding(vertical = 18.dp),
        title = "Theme",
        description = "System Default",
        icon = Icons.Outlined.BrightnessMedium,
        options = {
            Switch(
                checked = true, onCheckedChange = { })
        },
        onClick = null
    )
}