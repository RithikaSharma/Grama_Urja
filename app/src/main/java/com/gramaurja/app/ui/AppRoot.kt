package com.gramaurja.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.gramaurja.app.data.Supa
import com.gramaurja.app.data.repo.Repo
import com.gramaurja.app.ui.nav.Dest
import com.gramaurja.app.ui.screens.*
import com.gramaurja.app.util.I18n.t
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppRoot() {
    var loggedIn by rememberSaveable { mutableStateOf(false) }
    var checking by remember { mutableStateOf(true) }
    var isAdmin by remember { mutableStateOf(false) }
    var langTick by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        Supa.client.auth.sessionStatus.collectLatest { st ->
            checking = false
            when (st) {
                is SessionStatus.Authenticated -> {
                    loggedIn = true
                    val uid = Repo.currentUserId()
                    if (uid != null) isAdmin = runCatching { Repo.isAdmin(uid) }.getOrDefault(false)
                }
                else -> { loggedIn = false; isAdmin = false }
            }
        }
    }

    if (checking) {
        Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (!loggedIn) {
        AuthScreen(onLangChange = { langTick++ })
        return
    }

    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val current = backStack?.destination?.route

    val items = buildList {
        add(Triple(Dest.Home, Icons.Filled.Home, t("home")))
        add(Triple(Dest.Zones, Icons.Filled.Place, t("zones")))
        add(Triple(Dest.Pump, Icons.Filled.Timer, t("pump")))
        if (isAdmin) add(Triple(Dest.Admin, Icons.Filled.AdminPanelSettings, t("admin")))
        add(Triple(Dest.Settings, Icons.Filled.Settings, t("settings")))
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(t("appName")) })
        },
        bottomBar = {
            NavigationBar {
                items.forEach { (dest, icon, label) ->
                    NavigationBarItem(
                        selected = current == dest.route,
                        onClick = {
                            nav.navigate(dest.route) {
                                popUpTo(nav.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true; restoreState = true
                            }
                        },
                        icon = { Icon(icon as ImageVector, contentDescription = label) },
                        label = { Text(label, maxLines = 1) }
                    )
                }
            }
        }
    ) { pad ->
        Box(Modifier.padding(pad)) {
            key(langTick) {
                NavHost(nav, startDestination = Dest.Home.route) {
                    composable(Dest.Home.route) { HomeScreen(isAdmin = isAdmin) }
                    composable(Dest.Zones.route) { ZonesScreen() }
                    composable(Dest.Pump.route) { PumpScreen() }
                    composable(Dest.Admin.route) { if (isAdmin) AdminScreen() }
                    composable(Dest.Settings.route) { SettingsScreen(onLangChange = { langTick++ }) }
                }
            }
        }
    }
}
