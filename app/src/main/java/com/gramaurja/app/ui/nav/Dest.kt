package com.gramaurja.app.ui.nav

sealed class Dest(val route: String, val label: String) {
    data object Home : Dest("home", "Home")
    data object Zones : Dest("zones", "Zones")
    data object Pump : Dest("pump", "Pump")
    data object Admin : Dest("admin", "Admin")
    data object Settings : Dest("settings", "Settings")
}
