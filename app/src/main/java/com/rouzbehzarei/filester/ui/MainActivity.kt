package com.rouzbehzarei.filester.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.rouzbehzarei.filester.R
import com.rouzbehzarei.filester.databinding.ActivityMainBinding

/**
 * Main Activity and entry point for the app.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout XML file using Binding object instance
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)

        // Retrieve NavController from the NavHostFragment
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Set as the app bar for the activity
        setSupportActionBar(binding.appBar)

        // Set up the app bar for use with the NavController
        binding.appBar.setupWithNavController(navController)
    }

    /**
     * Initializes the contents of the specified options menu.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_about -> {
                val action =
                    ViewPagerFragmentDirections.actionViewPagerFragmentToAboutFragment()
                navController.navigate(action)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
                true
            }
        }
    }

}