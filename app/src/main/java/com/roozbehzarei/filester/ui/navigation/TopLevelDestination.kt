package com.roozbehzarei.filester.ui.navigation

import com.roozbehzarei.filester.R
import kotlinx.serialization.Serializable

/**
 * Serializable route object representing the main screen destination.
 * Used for type-safe navigation.
 *
 * @see FilesterNavHost for usage in navigation graph
 */
@Serializable
data object MainRoute

/**
 * Serializable route object representing the settings screen destination.
 * Used for type-safe navigation.
 *
 * @see FilesterNavHost for usage in navigation graph
 */
@Serializable
data object SettingsRoute

/**
 * Serializable route object representing the about screen destination.
 * Used for type-safe navigation.
 *
 * @see FilesterNavHost for usage in navigation graph
 */
@Serializable
data object AboutRoute

/**
 * Represents top-level navigation destinations in the app.
 *
 * @property labelResource String resource ID for displaying text in UI components
 * @property route Navigation route associated with this destination
 *
 * @sample TopLevelDestination.MAIN Primary entry point
 * @sample TopLevelDestination.SETTINGS App settings and preferences screen
 * @sample TopLevelDestination.ABOUT App information screen
 */
enum class TopLevelDestination(
    val labelResource: Int,
    val route: Any
) {
    MAIN(
        labelResource = R.string.app_name,
        route = MainRoute
    ),
    SETTINGS(
        labelResource = R.string.settings,
        route = SettingsRoute
    ),
    ABOUT(
        labelResource = R.string.empty,
        route = AboutRoute
    )
}