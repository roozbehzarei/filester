package com.roozbehzarei.filester.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.roozbehzarei.filester.R
import com.roozbehzarei.filester.presentation.theme.FilesterAppTheme

/**
 * Radio-button picker hosted in an [AlertDialog] so the option list scrolls while the action
 * buttons stay pinned, which keeps the dialog usable on a phone in landscape.
 *
 * @param onOptionLinkClick when non-`null`, every option gets a trailing link button that passes
 * the option back; leave it `null` for options that have nothing to link to.
 * @param applyOnSelect when `true`, tapping an option calls [onConfirm] right away and the dialog
 * stays open with a single dismiss button. When `false`, the selection is local until the user
 * presses the confirm button.
 */
@Composable
fun <T> SingleChoiceDialog(
    title: String,
    options: List<T>,
    initialSelection: T,
    optionLabel: (T) -> String,
    onDismissRequest: () -> Unit,
    onConfirm: (T) -> Unit,
    modifier: Modifier = Modifier,
    optionDescription: ((T) -> String?)? = null,
    onOptionLinkClick: ((T) -> Unit)? = null,
    applyOnSelect: Boolean = false,
) {
    var selectedOption by remember { mutableStateOf(initialSelection) }

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = title,
                maxLines = 1,
            )
        },
        text = {
            Column(
                modifier =
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .selectableGroup(),
            ) {
                options.forEach { option ->
                    val isSelected = selectedOption == option
                    val description = optionDescription?.invoke(option)
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(MaterialTheme.shapes.small)
                                .selectable(
                                    selected = isSelected,
                                    onClick = {
                                        selectedOption = option
                                        if (applyOnSelect) onConfirm(option)
                                    },
                                    role = Role.RadioButton,
                                ).defaultMinSize(minHeight = 48.dp)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = isSelected,
                                onClick = null,
                            )
                            Text(
                                modifier =
                                    Modifier
                                        .padding(start = 12.dp)
                                        .weight(1f),
                                text = optionLabel(option),
                                maxLines = 1,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            if (onOptionLinkClick != null) {
                                IconButton(onClick = { onOptionLinkClick(option) }) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_filled_link_2),
                                        contentDescription =
                                            stringResource(
                                                R.string.settings_action_open_website,
                                                optionLabel(option),
                                            ),
                                    )
                                }
                            }
                        }
                        if (description != null) {
                            Text(
                                modifier = Modifier.padding(start = 36.dp, top = 4.dp),
                                text = description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (!applyOnSelect) onConfirm(selectedOption)
                onDismissRequest()
            }) {
                Text(stringResource(if (applyOnSelect) R.string.close else R.string.apply))
            }
        },
        dismissButton =
            if (applyOnSelect) {
                null
            } else {
                {
                    TextButton(onClick = onDismissRequest) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            },
    )
}

@Preview
@Composable
private fun SingleChoiceDialogPreview() {
    val dummyOptions = listOf("English", "Persian", "Turkish")
    FilesterAppTheme {
        Surface {
            SingleChoiceDialog(
                title = "Language",
                options = dummyOptions,
                initialSelection = "English",
                optionLabel = { it },
                onDismissRequest = {},
                onConfirm = {},
            )
        }
    }
}
