package com.example.istanbulapp.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.example.istanbulapp.navigation.BottomNavItem
import com.example.istanbulapp.navigation.bottomNavItems

// ─────────────────────────────────────────────
//  📌 BOTTOM NAVIGATION BAR
//
//  The bar shown at the very bottom of the screen
//  with 3 tabs: Home, Explore, About.
//
//  📚 HOW IT WORKS:
//    - currentRoute: the route of the screen currently visible
//    - onNavItemClick: called with the new route when user taps a tab
//    - The selected tab is highlighted using MaterialTheme.colorScheme.primary
//
//  This is placed inside the Scaffold's `bottomBar` parameter
//  in MainActivity so it appears on every top-level screen.
// ─────────────────────────────────────────────

@Composable
fun IstanbulBottomNavBar(
    currentRoute: String?,                 // e.g. "home", "explore", "about"
    onNavItemClick: (String) -> Unit       // called when a tab is tapped
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = androidx.compose.ui.unit.Dp.Hairline
    ) {
        // Loop through each of the 3 bottom nav items
        bottomNavItems.forEach { item: BottomNavItem ->

            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick  = { onNavItemClick(item.route) },
                icon     = {
                    Icon(
                        imageVector        = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text       = item.label,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor   = MaterialTheme.colorScheme.primary,
                    indicatorColor      = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
