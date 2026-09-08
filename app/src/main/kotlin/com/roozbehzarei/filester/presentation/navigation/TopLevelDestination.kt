package com.roozbehzarei.filester.presentation.navigation

import androidx.navigation3.runtime.NavKey
import com.roozbehzarei.filester.R
import kotlinx.serialization.Serializable

/**
 * Sealed interface representing all navigation routes in the application.
 */
@Serializable
sealed interface Route : NavKey

/**
 * Serializable route object representing the uploads screen destination.
 * Used for type-safe navigation.
 *
 * @see FilesterNavHost for usage in navigation graph
 */
@Serializable
data object UploadsRoute : Route

/**
 * Serializable route object representing the settings screen destination.
 * Used for type-safe navigation.
 *
 * @see FilesterNavHost for usage in navigation graph
 */
@Serializable
data object SettingsRoute : Route

/**
 * Serializable route object representing the about screen destination.
 * Used for type-safe navigation.
 *
 * @see FilesterNavHost for usage in navigation graph
 */
@Serializable
data object AboutRoute : Route

/**
 * Represents top-level navigation destinations in the app.
 *
 * @property labelResource String resource ID for displaying text in UI components
 * @property route Navigation route associated with this destination
 *
 * @see TopLevelDestination.UPLOADS Primary entry point
 * @see TopLevelDestination.SETTINGS App settings and preferences screen
 * @see TopLevelDestination.ABOUT App information screen
 */
enum class TopLevelDestination(
    val labelResource: Int,
    val route: Route,
) {
    UPLOADS(
        labelResource = R.string.app_name,
        route = UploadsRoute,
    ),
    SETTINGS(
        labelResource = R.string.settings,
        route = SettingsRoute,
    ),
    ABOUT(
        labelResource = R.string.empty,
        route = AboutRoute,
    ),
}
