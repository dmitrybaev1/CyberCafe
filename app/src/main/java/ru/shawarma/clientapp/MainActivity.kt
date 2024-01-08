package ru.shawarma.clientapp

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.shawarma.core.data.entities.FirebaseTokenRequest
import ru.shawarma.core.data.repositories.OrderRepository
import ru.shawarma.core.ui.AppNavigation
import ru.shawarma.core.ui.CommonComponentsController
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), AppNavigation,
    CommonComponentsController {

    private lateinit var toolbar: Toolbar
    private lateinit var navController: NavController

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Snackbar.make(
                window.decorView.rootView,
                "Необходимо выдать разрешение на показ уведомлений",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            if(ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }

    }

    override fun sendFirebaseToken(sendAction: (String) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("Firebase", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val token = task.result
            sendAction.invoke(token)
        })
    }

    override fun deleteFirebaseToken() {
        FirebaseMessaging.getInstance().deleteToken()
    }

    override fun navigateToMenu() {
        navController.navigate(ru.shawarma.menu.R.id.menu_nav_graph)
    }

    override fun navigateToAuth() {
        val request = NavDeepLinkRequest.Builder
            .fromUri("android-app://ru.shawarma.app/authFragment".toUri())
            .build()
        navController.navigate(request)
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

    override fun navigateToOrder(orderId: Int) {
        val bundle = bundleOf("orderId" to orderId)
        navController.navigate(ru.shawarma.order.R.id.order_nav_graph,bundle)
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

    override fun showNoInternetSnackbar(view: View) {
        Snackbar.make(view, ru.shawarma.core.ui.R.string.no_internet,Snackbar.LENGTH_LONG).show()
    }
}