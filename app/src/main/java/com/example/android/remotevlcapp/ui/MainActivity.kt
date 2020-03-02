package com.example.android.remotevlcapp.ui

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.android.core.util.updateForTheme
import com.example.android.remotevlcapp.R
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : DaggerAppCompatActivity(), NavigationHost {

    companion object {
        private val TOP_LEVEL_DESTINATIONS = setOf(
            R.id.navigation_remote,
            R.id.navigation_browse,
            R.id.navigation_hosts,
            R.id.navigation_settings
        )
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: MainActivityViewModel

    private lateinit var navController: NavController
    private lateinit var drawer: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel =
            ViewModelProvider(this, viewModelFactory).get(MainActivityViewModel::class.java)

        drawer = findViewById(R.id.drawer_layout)
        navController = findNavController(R.id.nav_host_fragment)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isTopLevelDestination = TOP_LEVEL_DESTINATIONS.contains(destination.id)

            val lockMode = if (isTopLevelDestination) {
                nav_view.setCheckedItem(destination.id)
                DrawerLayout.LOCK_MODE_UNLOCKED
            } else {
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED
            }
            drawer_layout.setDrawerLockMode(lockMode)
        }

        nav_view.setNavigationItemSelectedListener { item ->
            if (item.isChecked) {
                drawer.closeDrawer(GravityCompat.START)
                return@setNavigationItemSelectedListener true
            }

            val options = NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setEnterAnim(android.R.animator.fade_in)
                .setExitAnim(android.R.animator.fade_out)
                .setPopEnterAnim(android.R.animator.fade_in)
                .setPopExitAnim(android.R.animator.fade_out)
                .setPopUpTo(R.id.navigation_remote, false)
                .build()

            val handled = when (item.itemId) {
                R.id.navigation_remote -> {
                    navController.navigate(R.id.navigation_remote, null, options)
                    true
                }
                R.id.navigation_browse -> {
                    navController.navigate(R.id.navigation_browse, null, options)
                    true
                }
                R.id.navigation_hosts -> {
                    navController.navigate(R.id.navigation_hosts, null, options)
                    true
                }
                R.id.navigation_settings -> {
                    navController.navigate(R.id.navigation_settings, null, options)
                    true
                }
                else -> false
            }
            drawer.closeDrawer(GravityCompat.START)
            return@setNavigationItemSelectedListener handled
        }

        // Automatically handles destination changes when a menu item is selected
        // But can't configure the default animation
//        nav_view.setupWithNavController(navController)

        if (savedInstanceState == null) {
            nav_view.setCheckedItem(R.id.navigation_remote)
        }

        viewModel.theme.observe(this, Observer(::updateForTheme))

    }

    override fun registerToolbarWithNavigation(toolbar: Toolbar) {
        val appBarConfiguration = AppBarConfiguration(TOP_LEVEL_DESTINATIONS, drawer)
        toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.shouldKeepScreenOn) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}
