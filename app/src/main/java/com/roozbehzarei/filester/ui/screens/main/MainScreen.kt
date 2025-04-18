package com.roozbehzarei.filester.ui.screens.main

import android.Manifest
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.work.WorkInfo
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.roozbehzarei.filester.R
import com.roozbehzarei.filester.data.local.File
import com.roozbehzarei.filester.ui.SharedViewModel
import kotlinx.coroutines.launch
import org.apache.commons.io.FileUtils
import org.koin.compose.koinInject

@Composable
fun MainScreen(
    viewModel: SharedViewModel = koinInject()
) {
    val uiState by viewModel.mainUiState.collectAsState()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        RequestNotificationPermission()
    }
    LaunchedEffect(uiState.uploadStatus) {
        when (uiState.uploadStatus) {
            WorkInfo.State.RUNNING -> viewModel.setUploadFabVisibility(false)
            WorkInfo.State.SUCCEEDED -> viewModel.setUploadFabVisibility(true)
            WorkInfo.State.FAILED -> viewModel.setUploadFabVisibility(true)
            WorkInfo.State.CANCELLED -> viewModel.setUploadFabVisibility(true)
            else -> viewModel.setUploadFabVisibility(true)
        }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        if (uiState.files.isEmpty() && uiState.uploadStatus != WorkInfo.State.RUNNING) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card {
                    Text(
                        modifier = Modifier.padding(12.dp),
                        text = stringResource(R.string.text_empty_history),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        FilesList(
            files = uiState.files,
            isUploadingFile = uiState.uploadStatus == WorkInfo.State.RUNNING,
            uploadingFileName = uiState.uploadingFileName,
            onCancelUploadRequest = {
                viewModel.cancelUpload()
            },
            onRemoveFileRequest = { file ->
                viewModel.deleteFile(file)
            })
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun RequestNotificationPermission() {
    val notificationPermissionState =
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    LaunchedEffect(Unit) {
        notificationPermissionState.launchPermissionRequest()
    }
    if (notificationPermissionState.status.shouldShowRationale) {
        // TODO: Show a bottom sheet indicating notifications permission is needed
    }
}

@Composable
private fun FilesList(
    modifier: Modifier = Modifier,
    files: List<File>,
    uploadingFileName: String?,
    isUploadingFile: Boolean,
    onCancelUploadRequest: () -> Unit,
    onRemoveFileRequest: (file: File) -> Unit
) {
    val context = LocalContext.current
    val snackbarHostState: SnackbarHostState = koinInject()
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val clipboardManager = LocalClipboardManager.current
    var fileToRemove by remember { mutableStateOf<File?>(null) }
    var selectedItemById by remember { mutableIntStateOf(-1) }
    LazyColumn(modifier = modifier, state = listState) {
        if (isUploadingFile) {
            item {
                FileUploadCard(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    fileName = uploadingFileName ?: "",
                    onCancel = onCancelUploadRequest,
                )
            }
            scope.launch {
                if (listState.canScrollBackward) listState.animateScrollToItem(0)
            }
        }
        items(items = files, key = { file ->
            file.id
        }) { file ->
            FileItem(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                name = file.name,
                size = file.size,
                icon = getFileIconByMimeType(file.mimeType),
                isExpanded = selectedItemById == file.id,
                onClick = {
                    selectedItemById = if (selectedItemById == file.id) -1 else file.id
                },
                onShare = {
                    shareFileLink(context = context, link = file.url)
                },
                onCopy = {
                    copyFileLink(clipboardManager, file.url)
                    scope.launch { snackbarHostState.showSnackbar(context.getString(R.string.snackbar_clipboard)) }
                },
                onRemove = {
                    fileToRemove = file
                })
            fileToRemove?.let {
                FileRemoverDialog(
                    fileName = file.name,
                    onDismissRequest = { fileToRemove = null },
                    onConfirmation = {
                        onRemoveFileRequest(fileToRemove!!)
                    })
            }
        }
    }
}

@Composable
private fun FileItem(
    modifier: Modifier = Modifier,
    name: String,
    size: Long,
    icon: Painter,
    isExpanded: Boolean,
    onClick: () -> Unit,
    onShare: () -> Unit,
    onCopy: () -> Unit,
    onRemove: () -> Unit,
) {
    val cardColor = when (isExpanded) {
        true -> MaterialTheme.colorScheme.surfaceVariant
        false -> Color.Transparent
    }
    Card(
        modifier = modifier, onClick = {
            onClick()
        }, colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column {
            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    modifier = Modifier.size(36.dp), painter = icon, contentDescription = null
                )
                Column(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        FileUtils.byteCountToDisplaySize(size),
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1
                    )
                }
            }
            AnimatedVisibility(isExpanded) {
                Row(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { onShare() },
                        colors = ButtonDefaults.filledTonalButtonColors()
                            .copy(containerColor = Color.Transparent)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Outlined.Share,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                stringResource(R.string.share),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Button(
                        onClick = { onCopy() },
                        colors = ButtonDefaults.filledTonalButtonColors()
                            .copy(containerColor = Color.Transparent)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Outlined.ContentCopy,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                stringResource(R.string.copy_address),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Button(
                        onClick = { onRemove() },
                        colors = ButtonDefaults.filledTonalButtonColors()
                            .copy(containerColor = Color.Transparent)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Outlined.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                stringResource(R.string.button_delete),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FileUploadCard(modifier: Modifier = Modifier, fileName: String, onCancel: () -> Unit) {
    Card(
        modifier = modifier
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier.size(36.dp),
                painter = painterResource(R.drawable.ic_filled_file_arrow_up),
                contentDescription = null
            )
            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    "Uploading $fileName",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                LinearProgressIndicator(
                    trackColor = Color.Transparent
                )
            }
            TextButton(
                onClick = {
                    onCancel()
                }) {
                Text(
                    stringResource(R.string.button_cancel),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun FileRemoverDialog(
    fileName: String, onDismissRequest: () -> Unit, onConfirmation: () -> Unit
) {
    AlertDialog(onDismissRequest = onDismissRequest, icon = {
        Icon(Icons.Outlined.DeleteForever, null)
    }, title = {
        Text(stringResource(R.string.dialog_delete_title))
    }, text = {
        Text(stringResource(R.string.dialog_delete_message, fileName))
    }, confirmButton = {
        TextButton(
            onClick = {
                onConfirmation()
                onDismissRequest()
            }) {
            Text(stringResource(R.string.button_delete))
        }
    }, dismissButton = {
        TextButton(
            onClick = {
                onDismissRequest()
            }) {
            Text(stringResource(R.string.dialog_button_close))
        }
    })
}

private fun shareFileLink(context: Context, link: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            "$link\n\n${context.getString(R.string.message_shared_with_filester)}"
        )
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    shareIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
    context.startActivity(shareIntent)
}

private fun copyFileLink(clipboardManager: ClipboardManager, link: String) {
    val clipData = ClipData.newPlainText("plain text", link)
    val clipEntry = ClipEntry(clipData)
    clipboardManager.setClip(clipEntry)
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
private fun getFileIconByMimeType(mimeType: String?): Painter {
    return when {
        MimeTypes.isText(mimeType) -> painterResource(R.drawable.ic_filled_file_lines)
        MimeTypes.isImage(mimeType) -> painterResource(R.drawable.ic_filled_file_image)
        MimeTypes.isAudio(mimeType) -> painterResource(R.drawable.ic_filled_file_audio)
        MimeTypes.isVideo(mimeType) -> painterResource(R.drawable.ic_filled_file_video)
        else -> painterResource(R.drawable.ic_filled_file)
    }
}