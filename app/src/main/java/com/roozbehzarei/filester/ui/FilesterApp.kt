package com.roozbehzarei.filester.ui

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.roozbehzarei.filester.R
import com.roozbehzarei.filester.ui.navigation.FilesterNavHost
import com.roozbehzarei.filester.ui.navigation.SettingsRoute
import com.roozbehzarei.filester.ui.navigation.TopLevelDestination
import org.koin.compose.koinInject

private const val STATUS_URL = "https://roozbehzarei.github.io/filester-status"

/**
 * Main composable function that serves as the entry point for the Filester application.
 * Sets up the navigation structure, top app bar, and floating action button (FAB).
 *
 * @see Scaffold
 * @see TopBar
 * @see UploadFab
 * @see FilesterNavHost
 */
@SuppressLint("RestrictedApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilesterApp(context: Context) {
    val viewModel: SharedViewModel = koinInject()
    val snackbarHostState: SnackbarHostState = koinInject()
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Check if current route is the main screen
    val isMainRoute =
        backStackEntry?.destination?.hasRoute(TopLevelDestination.MAIN.route::class) == true
    // Find matching top-level destination for current route
    val currentDestination = TopLevelDestination.entries.firstOrNull {
        backStackEntry?.destination?.hasRoute(it.route::class) == true
    }
    val shouldShowUploadFab by viewModel.shouldShowUploadFab.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopBar(
                title = stringResource(currentDestination?.labelResource ?: R.string.empty),
                shouldShowMenu = isMainRoute,
                canNavigateUp = isMainRoute.not(),
                onNavigateUp = { navController.navigateUp() },
                onNetworkStatusClicked = {
                    try {
                        val intent = CustomTabsIntent.Builder().build()
                        intent.launchUrl(
                            context, STATUS_URL.toUri()
                        )
                    } catch (_: Exception) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.toast_app_not_found),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                onNavigateToSettings = { navController.navigate(SettingsRoute) },
                onNavigateToAbout = { navController.navigate(TopLevelDestination.ABOUT.route) },
            )
        },
        floatingActionButton = {
            // Show FAB only on main screen
            if (isMainRoute && shouldShowUploadFab) {
                UploadFab(viewModel::initializeUpload)
            }
        }) { innerPadding ->
        FilesterNavHost(
            modifier = Modifier.padding(innerPadding), navController = navController
        )
    }

}

/**
 * @param title The text to display in the app bar
 * @param shouldShowMenu Whether to show the overflow menu (for main screen)
 * @param canNavigateUp Whether to show back navigation arrow
 * @param onNavigateUp Callback for back navigation
 * @param onNetworkStatusClicked Callback for handling network status icon clicks.
 *                                Launches a Custom Tab with the status page URL.
 * @param onNavigateToSettings Callback for navigating to settings screen
 * @param onNavigateToAbout Callback for navigating to about screen
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TopBar(
    title: String,
    shouldShowMenu: Boolean,
    canNavigateUp: Boolean,
    onNavigateUp: () -> Unit,
    onNetworkStatusClicked: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit
) {
    CenterAlignedTopAppBar(title = { Text(title) }, actions = {
        if (shouldShowMenu) {
            IconButton(
                onClick = onNetworkStatusClicked
            ) {
                Icon(Icons.Filled.NetworkCheck, null)
            }
            OverflowMenu({
                DropdownMenuItem(
                    onClick = onNavigateToSettings,
                    text = { Text(stringResource(R.string.settings)) })
                DropdownMenuItem(
                    onClick = onNavigateToAbout,
                    text = { Text(stringResource(R.string.menu_about)) })
            })
        }
    }, navigationIcon = {
        if (canNavigateUp) {
            IconButton(onClick = onNavigateUp) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
        }
    })
}

/**
 * Custom overflow menu component with dropdown functionality.
 *
 * @param menuItems A composable function that defines the content of the dropdown menu.
 *                  This lambda should contain the Composable elements (e.g., `DropdownMenuItem`)
 *                  that will be displayed in the menu.
 */
@Composable
private fun OverflowMenu(menuItems: @Composable () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }

    IconButton(onClick = {
        showMenu = !showMenu
    }) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = null,
        )
    }
    DropdownMenu(
        expanded = showMenu, onDismissRequest = { showMenu = false }) {
        menuItems()
    }
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