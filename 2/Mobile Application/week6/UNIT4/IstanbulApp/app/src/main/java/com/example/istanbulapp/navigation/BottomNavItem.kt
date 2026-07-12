package com.example.istanbulapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.ui.graphics.vector.ImageVector

// ─────────────────────────────────────────────
//  📌 BOTTOM NAVIGATION BAR ITEMS
//
//  The bottom nav bar shows 3 tabs:
//    1. Home    → shows category grid
//    2. Explore → shows all 15 places in a list
//    3. About   → shows Istanbul facts
//
//  📚 KEY CONCEPT — data class:
//    A data class is perfect for holding simple structured data.
//    Each BottomNavItem has a label, icon, and the route to navigate to.
// ─────────────────────────────────────────────

data class BottomNavItem(
    val label: String,       // Text shown below the icon
    val icon: ImageVector,   // The icon (from Material Icons library)
    val route: String        // Which Screen to navigate to when tapped
)

/**
 * The 3 items that appear in the bottom navigation bar.
 * This is a top-level val — available anywhere in the navigation package.
 */
val bottomNavItems = listOf(
    BottomNavItem(
        label = "Home",
        icon  = Icons.Filled.Home,
        route = Screen.Home.route
    ),
    BottomNavItem(
        label = "Explore",
        icon  = Icons.Filled.Place,
        route = Screen.Explore.route
    ),
    BottomNavItem(
        label = "About",
        icon  = Icons.Filled.Info,
        route = Screen.About.route
    )
)
