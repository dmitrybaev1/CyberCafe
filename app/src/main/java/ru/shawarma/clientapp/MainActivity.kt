package ru.shawarma.clientapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.shawarma.core.data.entities.AuthData
import ru.shawarma.core.ui.AppNavigation
import ru.shawarma.core.ui.CommonComponentsController
import ru.shawarma.core.ui.InternetConnectionStatus

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), AppNavigation,
    CommonComponentsController,InternetConnectionStatus {

    private lateinit var toolbar: Toolbar
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        toolbar = findViewById(R.id.toolbar)
        //setSupportActionBar(toolbar)
        val appBarConfig = AppBarConfiguration(navController.graph)
        toolbar.setupWithNavController(navController,appBarConfig)
    }

    override fun navigateToMenu(authData: AuthData) {
        val bundle = bundleOf("authData" to authData)
        navController.navigate(ru.shawarma.menu.R.id.menu_nav_graph, bundle)
    }

    override fun navigateToAuth(errorMessage: String) {
        val request = NavDeepLinkRequest.Builder
            .fromUri("android-app://ru.shawarma.app/authFragment/$errorMessage".toUri())
            .build()
        navController.navigate(request)
    }

    override fun navigateToSettings() {
        navController.navigate(ru.shawarma.settings.R.id.settings_nav_graph)
    }

    override fun navigateToOrder() {
        navController.navigate(ru.shawarma.order.R.id.order_nav_graph)
    }

    override fun setupToolbarForInsideNavigation(
        navController: NavController?,
        subGraph: NavGraph
    ) {
        navController?.let {
            toolbar.setupWithNavController(it, AppBarConfiguration(subGraph))
        } ?: run{
            toolbar.setupWithNavController(this.navController, AppBarConfiguration(subGraph))
        }

    }

    override fun inflateToolbarMenu(
        menuRes: Int,
        onMenuItemClickListener: Toolbar.OnMenuItemClickListener
    ) {
        toolbar.menu.clear()
        toolbar.inflateMenu(menuRes)
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener)
    }

    override fun clearToolbarMenu() {
        toolbar.menu.clear()
    }

    override fun changeToolbarMenuItemClickListener(onMenuItemClickListener: Toolbar.OnMenuItemClickListener) {
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener)
    }

    override fun setCloseToolbarIcon() {
        toolbar.setNavigationIcon(com.google.android.material.R.drawable.ic_m3_chip_close)
    }

    override fun isOnline(): Boolean = true


}