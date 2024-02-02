package com.roozbehzarei.filester.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.appbar.AppBarLayout
import com.roozbehzarei.filester.R
import com.roozbehzarei.filester.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

/**
 * Main Activity and entry point for the app.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        // Inflate the layout XML file using Binding object instance
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /**
         * Display content edge-to-edge
         */
        // Lay out your app in full screen
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // Handle overlaps using insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.findViewById<AppBarLayout>(R.id.app_bar_layout).updatePadding(top = insets.top)
            view.updatePadding(
                left = insets.left,
                right = insets.right,
                bottom = insets.bottom,
            )
            // Do not pass window insets down to descendant views
            WindowInsetsCompat.CONSUMED
        }

        // Change the color of the navigation bars
        val windowInsetsController = WindowCompat.getInsetsController(window, binding.root)
        val currentNightMode =
            applicationContext.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> {
                windowInsetsController.isAppearanceLightNavigationBars = true
                windowInsetsController.isAppearanceLightStatusBars = true
            }

            Configuration.UI_MODE_NIGHT_YES -> {
                windowInsetsController.isAppearanceLightNavigationBars = false
                windowInsetsController.isAppearanceLightStatusBars = false
            }
        }

        // Set as the app bar for the activity
        setSupportActionBar(binding.appBar)

        // Retrieve NavController from the NavHostFragment
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Set up the app bar for use with the NavController
        binding.appBar.setupWithNavController(navController)

        createNotificationChannel()
    }

    private fun showUpdateDialog() {
        val updateDialog = UpdateDialog()
        if (!updateDialog.isAdded) updateDialog.show(
            supportFragmentManager, UpdateDialog.TAG
        )
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(
                getString(R.string.notification_channel_id), name, importance
            ).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}