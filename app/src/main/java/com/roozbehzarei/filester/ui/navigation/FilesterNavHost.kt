package com.roozbehzarei.filester.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.roozbehzarei.filester.ui.screens.about.AboutScreen
import com.roozbehzarei.filester.ui.screens.main.MainScreen
import com.roozbehzarei.filester.ui.screens.settings.SettingsScreen

/**
 * Serves as the navigation host for the application.
 *
 * Uses type-safe navigation with dedicated route classes/objects to represent
 * each destination, providing compile-time safety for navigation arguments.
 */
@Composable
fun FilesterNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {

    return NavHost(
        modifier = modifier, navController = navController, startDestination = MainRoute
    ) {
        composable<MainRoute> {
            MainScreen()
        }
        composable<SettingsRoute> {
            SettingsScreen()
        }
        composable<AboutRoute> {
            AboutScreen()
        }
    }
}