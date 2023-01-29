package ru.shawarma.clientapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import ru.shawarma.auth.AuthNavigation
import ru.shawarma.menu.MenuNavigation

class MainActivity : AppCompatActivity(), AuthNavigation, MenuNavigation {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val appBarConfig = AppBarConfiguration(navController.graph)
        toolbar.setupWithNavController(navController,appBarConfig)
    }

    override fun navigateToMenu() {
        navController.navigate(ru.shawarma.menu.R.id.menu_nav_graph)
    }

    override fun navigateToSettings() {
        navController.navigate(ru.shawarma.settings.R.id.settings_nav_graph)
    }

    override fun navigateToOrder() {
        navController.navigate(ru.shawarma.order.R.id.order_nav_graph)
    }


}