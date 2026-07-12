package com.example.istanbulapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.istanbulapp.navigation.IstanbulNavGraph
import com.example.istanbulapp.navigation.Screen
import com.example.istanbulapp.ui.components.IstanbulBottomNavBar
import com.example.istanbulapp.ui.theme.IstanbulAppTheme

// ─────────────────────────────────────────────
//  📱 MAIN ACTIVITY
//
//  This is the entry point of our Android app.
//  In Jetpack Compose, the activity sets the content view to a Composable function.
//
//  We set up:
//    1. Our custom IstanbulAppTheme
//    2. A NavController to manage navigation state
//    3. A Scaffold to manage window boundaries and layout structures like bottom bars
//    4. The bottom navigation bar, which changes its selected tab based on the active screen
// ─────────────────────────────────────────────

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enables borderless screen drawing (status bar merging)
        setContent {
            IstanbulAppTheme {
                // Initialize the NavController which tracks backstack and navigations
                val navController = rememberNavController()

                // Read current navigation backstack entry as state
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // professional touch: only show bottom bar on core top-level screens
                val showBottomBar = when (currentRoute) {
                    Screen.Home.route, Screen.Explore.route, Screen.About.route -> true
                    else -> false // Hide bottom bar on detail and category lists for focus
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            IstanbulBottomNavBar(
                                currentRoute = currentRoute,
                                onNavItemClick = { route ->
                                    // Navigate to selected tab, popping up to home to avoid stack build-up
                                    navController.navigate(route) {
                                        popUpTo(Screen.Home.route) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    // IstanbulNavGraph coordinates which screen is drawn in the content slot
                    IstanbulNavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}