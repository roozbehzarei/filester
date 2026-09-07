package com.roozbehzarei.filester.presentation

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import com.roozbehzarei.filester.R
import com.roozbehzarei.filester.presentation.components.rememberCustomTabsIntent
import com.roozbehzarei.filester.presentation.navigation.AboutRoute
import com.roozbehzarei.filester.presentation.navigation.FilesterNavHost
import com.roozbehzarei.filester.presentation.navigation.MainRoute
import com.roozbehzarei.filester.presentation.navigation.SettingsRoute
import com.roozbehzarei.filester.presentation.navigation.TopLevelDestination
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

private const val STATUS_URL = "https://filester.roozbehzarei.com/status"

private val navSavedStateConfiguration =
    SavedStateConfiguration {
        serializersModule =
            SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(MainRoute::class, MainRoute.serializer())
                    subclass(SettingsRoute::class, SettingsRoute.serializer())
                    subclass(AboutRoute::class, AboutRoute.serializer())
                }
            }
    }

/**
 * CompositionLocal providing [SnackbarHostState] to composable screens within the app hierarchy.
 */
val LocalSnackbarHostState =
    staticCompositionLocalOf<SnackbarHostState> {
        error("No SnackbarHostState provided")
    }

/**
 * Main composable function that serves as the entry point for the Filester application.
 * Sets up the navigation structure, top app bar, and floating action button (FAB).
 *
 * @see Scaffold
 * @see TopBar
 * @see FilesterNavHost
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilesterApp(
    context: Context,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val customTabsIntent = rememberCustomTabsIntent()
    val backStack = rememberNavBackStack(navSavedStateConfiguration, MainRoute)
    val currentRoute = backStack.lastOrNull()
    // Check if current route is the main screen
    val isMainRoute = currentRoute == MainRoute
    // Find matching top-level destination for current route
    val currentDestination =
        TopLevelDestination.entries.firstOrNull {
            it.route == currentRoute
        }

    CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            topBar = {
                TopBar(
                    title = stringResource(currentDestination?.labelResource ?: R.string.empty),
                    shouldShowMenu = isMainRoute,
                    canNavigateUp = isMainRoute.not(),
                    onNavigateUp = { backStack.removeLastOrNull() },
                    onNetworkStatusClick = {
                        try {
                            customTabsIntent.launchUrl(
                                context,
                                STATUS_URL
                                    .toUri()
                                    .buildUpon()
                                    .appendQueryParameter("app", "true")
                                    .build(),
                            )
                        } catch (_: Exception) {
                            Toast
                                .makeText(
                                    context,
                                    context.getString(R.string.toast_app_not_found),
                                    Toast.LENGTH_SHORT,
                                ).show()
                        }
                    },
                    onNavigateToSettings = { backStack.add(SettingsRoute) },
                    onNavigateToAbout = { backStack.add(AboutRoute) },
                )
            },
        ) { innerPadding ->
            FilesterNavHost(
                modifier = Modifier.padding(innerPadding),
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
            )
        }
    }
}

/**
 * @param title The text to display in the app bar
 * @param shouldShowMenu Whether to show the overflow menu (for main screen)
 * @param canNavigateUp Whether to show back navigation arrow
 * @param onNavigateUp Callback for back navigation
 * @param onNetworkStatusClick Callback for handling network status icon clicks.
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
    onNetworkStatusClick: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
) {
    CenterAlignedTopAppBar(title = { Text(title) }, actions = {
        if (shouldShowMenu) {
            IconButton(
                onClick = onNetworkStatusClick,
            ) {
                Icon(Icons.Filled.NetworkCheck, null)
            }
            OverflowMenu {
                DropdownMenuItem(
                    onClick = onNavigateToSettings,
                    text = { Text(stringResource(R.string.settings)) },
                )
                DropdownMenuItem(
                    onClick = onNavigateToAbout,
                    text = { Text(stringResource(R.string.main_menu_about)) },
                )
            }
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
private fun OverflowMenu(
    modifier: Modifier = Modifier,
    menuItems: @Composable () -> Unit,
) {
    var showMenu by remember { mutableStateOf(false) }

    IconButton(onClick = {
        showMenu = !showMenu
    }) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = null,
        )
    }
    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
        menuItems()
    }
}
