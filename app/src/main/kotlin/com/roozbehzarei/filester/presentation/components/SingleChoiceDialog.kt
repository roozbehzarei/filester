package com.roozbehzarei.filester.presentation.components

import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
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
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.roozbehzarei.filester.R
import com.roozbehzarei.filester.presentation.theme.FilesterAppTheme

@Composable
fun <T> SingleChoiceDialog(
    title: String,
    options: List<T>,
    initialSelection: T,
    optionLabel: (T) -> String,
    onDismissRequest: () -> Unit,
    onConfirm: (T) -> Unit,
    modifier: Modifier = Modifier,
    optionDescription: ((T) -> String?)? = null
) {
    var selectedOption by remember { mutableStateOf(initialSelection) }

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column {
                Text(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = title,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleLarge
                )
                options.forEach { option ->
                    val isSelected = selectedOption == option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = isSelected,
                                onClick = { selectedOption = option },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = if (optionDescription != null) Alignment.Top else CenterVertically
                    ) {
                        RadioButton(
                            modifier = Modifier.padding(12.dp),
                            selected = isSelected,
                            onClick = null
                        )
                        Column(
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                                .weight(1f)
                        ) {
                            Text(
                                text = optionLabel(option),
                                maxLines = 1,
                                style = MaterialTheme.typography.titleSmall
                            )
                            if (optionDescription != null) {
                                val description = optionDescription(option)
                                if (description != null) {
                                    Spacer(modifier = Modifier.padding(top = 4.dp))
                                    Text(
                                        text = description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
                Row(Modifier.padding(16.dp)) {
                    Spacer(Modifier.weight(1f))
                    TextButton(onClick = onDismissRequest) {
                        Text(stringResource(R.string.cancel))
                    }
                    TextButton(onClick = {
                        onConfirm(selectedOption)
                        onDismissRequest()
                    }) {
                        Text(stringResource(R.string.apply))
                    }
                }
            }
        }
    }
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
                onConfirm = {}
            )
        }
    }
}
