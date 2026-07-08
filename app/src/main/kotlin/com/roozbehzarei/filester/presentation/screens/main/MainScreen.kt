package com.roozbehzarei.filester.presentation.screens.main

import android.Manifest
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.text.format.Formatter
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.roozbehzarei.filester.R
import com.roozbehzarei.filester.domain.model.File
import com.roozbehzarei.filester.presentation.theme.FilesterAppTheme
import com.roozbehzarei.filester.upload.UploadState
import com.roozbehzarei.filester.upload.UploadStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.minutes

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = koinViewModel(),
    snackbarHostState: SnackbarHostState
) {
    val coroutineScope = rememberCoroutineScope()
    val uiState by viewModel.mainUiState.collectAsStateWithLifecycle()
    var showUploadFailDialog by rememberSaveable { mutableStateOf(false) }

    uiState.message?.let { uiText ->
        val message = uiText.asString()
        LaunchedEffect(uiText) {
            snackbarHostState.showSnackbar(message)
            viewModel.messageShown()
        }
    }

    LaunchedEffect(uiState.uploadStatus) {
        showUploadFailDialog = when (uiState.uploadStatus.state) {
            UploadState.RUNNING -> {
                false
            }

            UploadState.FAILED -> {
                true
            }

            else -> {
                false
            }
        }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        RequestNotificationPermission()
    }

    if (showUploadFailDialog) {
        UploadFailDialog(onDismissRequest = {
            viewModel.resetUploadStatus()
        })
    }

    MainContent(
        modifier = modifier.fillMaxSize(),
        uiState = uiState,
        onShowSnackbar = { message -> coroutineScope.launch { snackbarHostState.showSnackbar(message) } },
        onUploadCanceled = viewModel::cancelUpload,
        onFileRemoved = { viewModel.deleteFile(it) },
        onInitializeUpload = viewModel::initializeUpload
    )
}

@Composable
private fun MainContent(
    modifier: Modifier = Modifier,
    uiState: MainUiState,
    onShowSnackbar: (String) -> Unit,
    onUploadCanceled: () -> Unit,
    onFileRemoved: (File) -> Unit,
    onInitializeUpload: (Uri, String) -> Unit
) {

    Scaffold(
        modifier = modifier, floatingActionButton = {
            if (uiState.uploadStatus.state != UploadState.RUNNING) {
                UploadFab(onInitializeUpload)
            }
        }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.files.isEmpty() && uiState.uploadStatus.state != UploadState.RUNNING) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Card {
                        Text(
                            modifier = Modifier.padding(12.dp),
                            text = stringResource(R.string.main_text_empty_history),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            FilesList(
                files = uiState.files,
                isUploadingFile = uiState.uploadStatus.state == UploadState.RUNNING,
                uploadingFileName = uiState.uploadingFileName,
                uploadingFileProgress = uiState.uploadStatus.progress,
                onCancelUpload = onUploadCanceled,
                onRemoveFile = { file ->
                    onFileRemoved(file)
                },
                onShowSnackbar = { onShowSnackbar(it) })
        }
    }


}

@Composable
private fun UploadFailDialog(
    modifier: Modifier = Modifier, onDismissRequest: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        icon = { Icon(Icons.Outlined.ErrorOutline, null) },
        title = { Text(stringResource(R.string.title_upload_error)) },
        text = { Text(stringResource(R.string.main_text_upload_error)) },
        confirmButton = {
            TextButton(onClick = {
                onDismissRequest()
            }) {
                Text(text = stringResource(R.string.main_button_close))
            }
        },
        onDismissRequest = { onDismissRequest() })
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
}

@Composable
private fun FilesList(
    modifier: Modifier = Modifier,
    files: List<File>,
    isUploadingFile: Boolean,
    uploadingFileName: String?,
    uploadingFileProgress: Int?,
    onCancelUpload: () -> Unit,
    onRemoveFile: (file: File) -> Unit,
    onShowSnackbar: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val clipboard = LocalClipboard.current
    val copiedToClipboardMessage = stringResource(R.string.main_snackbar_clipboard)
    var fileToRemove by rememberSaveable { mutableStateOf<File?>(null) }
    var selectedFileId by rememberSaveable { mutableIntStateOf(-1) }

    LaunchedEffect(isUploadingFile) {
        if (isUploadingFile) {
            listState.animateScrollToItem(0)
        }
    }

    LazyColumn(modifier = modifier, state = listState) {
        if (isUploadingFile) {
            item {
                FileUploadCard(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    fileName = uploadingFileName ?: "",
                    uploadProgress = uploadingFileProgress ?: 0,
                    onCancel = onCancelUpload,
                )
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
                expiresAt = file.expiresAt,
                icon = fileIconByMimeType(file.mimeType),
                isExpanded = selectedFileId == file.id,
                onClick = {
                    selectedFileId = if (selectedFileId == file.id) -1 else file.id
                },
                onShare = {
                    shareFileLink(context = context, downloadUrl = file.downloadUrl)
                },
                onCopy = {
                    scope.launch { copyFileLink(clipboard, file.downloadUrl) }
                    onShowSnackbar(copiedToClipboardMessage)
                },
                onRemove = {
                    fileToRemove = file
                })
        }
    }

    fileToRemove?.let { file ->
        FileRemoverDialog(
            fileName = file.name,
            onDismissRequest = { fileToRemove = null },
            onConfirmation = {
                onRemoveFile(file)
                fileToRemove = null
            })
    }
}

@Composable
private fun FileItem(
    modifier: Modifier = Modifier,
    name: String,
    size: Long,
    expiresAt: Long,
    icon: Painter,
    isExpanded: Boolean,
    onClick: () -> Unit,
    onShare: () -> Unit,
    onCopy: () -> Unit,
    onRemove: () -> Unit,
) {
    val context = LocalContext.current
    val formattedSize = remember { Formatter.formatFileSize(context, size) }
    val now by produceState(initialValue = System.currentTimeMillis()) {
        while (true) {
            delay(1.minutes)
            value = System.currentTimeMillis()
        }
    }
    val remainingHours = remember(expiresAt, now) {
        if (expiresAt <= 0L) null
        else {
            val remainingMs = expiresAt - now
            if (remainingMs <= 0) -1 else TimeUnit.MILLISECONDS.toHours(remainingMs).toInt()
        }
    }
    val expiresText = when (remainingHours) {
        null -> null
        -1 -> stringResource(R.string.main_text_expired)
        0 -> stringResource(R.string.main_text_expires_soon)
        else -> pluralStringResource(
            R.plurals.main_text_expires_hours, remainingHours, remainingHours
        )
    }

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
                    val infoText = if (expiresText != null) {
                        "$formattedSize • $expiresText"
                    } else {
                        formattedSize
                    }
                    Text(
                        infoText,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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
                    FileItemAction(
                        onClick = onShare,
                        icon = Icons.Outlined.Share,
                        label = stringResource(R.string.main_button_share)
                    )
                    FileItemAction(
                        onClick = onCopy,
                        icon = Icons.Outlined.ContentCopy,
                        label = stringResource(R.string.main_button_copy)
                    )
                    FileItemAction(
                        onClick = onRemove,
                        icon = Icons.Outlined.Delete,
                        label = stringResource(R.string.main_button_delete)
                    )
                }
            }
        }
    }
}

@Composable
private fun FileItemAction(
    onClick: () -> Unit,
    icon: ImageVector,
    label: String,
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.filledTonalButtonColors().copy(containerColor = Color.Transparent)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FileUploadCard(
    modifier: Modifier = Modifier, fileName: String, uploadProgress: Int, onCancel: () -> Unit
) {
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
                    modifier = Modifier.fillMaxWidth(), progress = {
                        (uploadProgress.toFloat() / 100)
                    }, trackColor = Color.Transparent
                )
            }
            TextButton(
                onClick = {
                    onCancel()
                }) {
                Text(
                    stringResource(R.string.cancel), style = MaterialTheme.typography.labelLarge
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
        Text(stringResource(R.string.main_dialog_title_delete))
    }, text = {
        Text(stringResource(R.string.main_dialog_text_delete, fileName))
    }, confirmButton = {
        TextButton(
            onClick = {
                onConfirmation()
                onDismissRequest()
            }) {
            Text(stringResource(R.string.main_button_delete))
        }
    }, dismissButton = {
        TextButton(
            onClick = {
                onDismissRequest()
            }) {
            Text(stringResource(R.string.main_button_close))
        }
    })
}

/**
 * Displays a Floating Action Button (FAB) for uploading files.
 *
 * When clicked, it launches a file picker allowing the user to select a file.
 * Upon selection, it extracts the file's URI and name and invokes the provided `onUpload` callback.
 *
 * @param onUpload A lambda function that is invoked when a file is selected.
 */
@Composable
private fun UploadFab(onUpload: (uri: Uri, name: String) -> Unit) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                val name = cursor.getString(nameIndex)
                onUpload(uri, name)
            }
        }
    }
    FloatingActionButton(
        onClick = {
            launcher.launch("*/*")
        },
    ) {
        Icon(Icons.Filled.Add, contentDescription = "Upload File")
    }
}

@Preview
@Composable
private fun MainContentPreview() {
    FilesterAppTheme {
        Surface {
            MainContent(
                modifier = Modifier.fillMaxSize(),
                uiState = MainUiState(),
                onShowSnackbar = {},
                onUploadCanceled = {},
                onFileRemoved = {},
                onInitializeUpload = { _, _ -> })
        }
    }
}

@Preview
@Composable
private fun MainContentPreview2() {

    val videoFile = File(
        id = 0,
        name = "Assassin's Creed Black Flag Resynced Official Game Overview Trailer.mp4",
        downloadUrl = "",
        size = 1020000000,
        mimeType = "video/mp4"
    )
    val audioFile = File(
        id = 1,
        name = "Self Conscious - Prodigy, Nas.flac",
        downloadUrl = "",
        size = 17400000,
        mimeType = "audio/flac"
    )
    val previewFiles = listOf(videoFile, audioFile)

    FilesterAppTheme {
        Surface {
            MainContent(
                modifier = Modifier.fillMaxSize(),
                uiState = MainUiState(
                    files = previewFiles,
                    uploadStatus = UploadStatus(state = UploadState.RUNNING, 33),
                    uploadingFileName = "filester.apk"
                ),
                onShowSnackbar = {},
                onUploadCanceled = {},
                onFileRemoved = {},
                onInitializeUpload = { _, _ -> })
        }
    }
}

@Preview
@Composable
private fun UploadFailDialogPreview() {
    FilesterAppTheme {
        UploadFailDialog(onDismissRequest = {})
    }
}

@Preview
@Composable
private fun FileItemPreview() {
    FilesterAppTheme {
        FileItem(
            name = "android.mp4",
            size = 2048,
            expiresAt = 0L,
            icon = painterResource(R.drawable.ic_filled_file_video),
            isExpanded = true,
            onClick = {},
            onShare = {},
            onCopy = {},
            onRemove = {})
    }
}

@Preview
@Composable
private fun FileUploadCardPreview() {
    FilesterAppTheme {
        FileUploadCard(fileName = "android.mp4", uploadProgress = 50) { }
    }
}

@Preview
@Composable
private fun FileRemoverDialogPreview() {
    FilesterAppTheme {
        FileRemoverDialog(
            fileName = "android.mp4", onDismissRequest = {}) {}
    }
}

private fun shareFileLink(context: Context, downloadUrl: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            "$downloadUrl\n\n${context.getString(R.string.main_text_shared_with_filester)}"
        )
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    shareIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
    context.startActivity(shareIntent)
}

private suspend fun copyFileLink(clipboard: Clipboard, downloadUrl: String) {
    val clipData = ClipData.newPlainText("plain text", downloadUrl)
    val clipEntry = ClipEntry(clipData)
    clipboard.setClipEntry(clipEntry)
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
private fun fileIconByMimeType(mimeType: String?): Painter {
    return when {
        MimeTypes.isText(mimeType) -> painterResource(R.drawable.ic_filled_file_lines)
        MimeTypes.isImage(mimeType) -> painterResource(R.drawable.ic_filled_file_image)
        MimeTypes.isAudio(mimeType) -> painterResource(R.drawable.ic_filled_file_audio)
        MimeTypes.isVideo(mimeType) -> painterResource(R.drawable.ic_filled_file_video)
        else -> painterResource(R.drawable.ic_filled_file)
    }
}