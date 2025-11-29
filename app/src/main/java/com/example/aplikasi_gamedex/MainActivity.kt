package com.example.aplikasi_gamedex

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.ui.setupWithNavController
import com.example.aplikasi_gamedex.databinding.ActivityMainBinding
import com.google.android.material.appbar.AppBarLayout

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        applySavedTheme()   // ⬅ wajib sebelum super.onCreate()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.SplashFragment,
                R.id.ViewPagerFragment,
                R.id.SalesFragment,
                R.id.SettingsFragment,
                R.id.FavoritesFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        // bottom nav
        binding.bottomNav.setupWithNavController(navController)

        // Kontrol visibilitas toolbar & bottom nav
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.appBarLayout.setExpanded(true, true)
            when (destination.id) {
                R.id.SplashFragment, R.id.ViewPagerFragment -> {
                    binding.toolbar.visibility = View.GONE
                    binding.bottomNav.visibility = View.GONE
                }

                R.id.DetailsFragment,
                R.id.DetailsGamesFragment -> {
                    binding.toolbar.menu.findItem(R.id.action_detail)?.isVisible = false
                    binding.bottomNav.visibility = View.GONE
                }

                else -> {
                    binding.toolbar.visibility = View.VISIBLE
                    binding.toolbar.menu.findItem(R.id.action_detail)?.isVisible = true
                    binding.bottomNav.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_top, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_detail -> {
                findNavController(R.id.nav_host_fragment_content_main)
                    .navigate(R.id.action_to_DetailsFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * FIX tema agar tidak kedip-kedip saat navigate
     */
    private fun applySavedTheme() {
        val prefs = getSharedPreferences("theme_prefs", MODE_PRIVATE)
        val isDark = prefs.getBoolean("dark_mode", false)

        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
