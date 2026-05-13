package com.gramaurja.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gramaurja.app.data.LocalPrefs
import com.gramaurja.app.service.PowerWatchService
import com.gramaurja.app.ui.AppRoot
import com.gramaurja.app.ui.WelcomeScreen
import com.gramaurja.app.ui.theme.GramaUrjaTheme

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Reset memory on startup to ensure we catch the first event
        LocalPrefs.lastKnownStatus = null
        // Updated to the UUID showing in your logs for your Bantwal zone
        LocalPrefs.watchedZoneId = "173d0aba-b2e7-4cfe-8496-b2068d5b48bf"

        checkNotificationPermission()

        setContent {
            GramaUrjaTheme {
                val currentLang by LocalPrefs.langSignal.collectAsState()
                key(currentLang) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "welcome") {
                        composable("welcome") {
                            WelcomeScreen(onGetStarted = {
                                startPowerService()
                                navController.navigate("main_app") {
                                    popUpTo("welcome") { inclusive = true }
                                }
                            })
                        }
                        composable("main_app") { AppRoot() }
                    }
                }
            }
        }
    }

    private fun startPowerService() {
        val intent = Intent(this, PowerWatchService::class.java)
        ContextCompat.startForegroundService(this, intent)
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}