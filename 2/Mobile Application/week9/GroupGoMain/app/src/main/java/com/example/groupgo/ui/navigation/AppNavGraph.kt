package com.example.groupgo.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.groupgo.ui.balance.BalanceScreen
import com.example.groupgo.ui.groups.GroupsScreen
import com.example.groupgo.ui.history.HistoryScreen
import com.example.groupgo.ui.home.HomeScreen
import com.example.groupgo.ui.settings.SettingsScreen
import com.example.groupgo.ui.login.LoginScreen
import com.example.groupgo.domain.repository.AuthRepository

sealed class AppDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    data object Home : AppDestination("home", "Home", Icons.Filled.Home)
    data object Groups : AppDestination("groups", "Groups", Icons.Filled.Group)
    data object History : AppDestination("history", "History", Icons.Filled.History)
    data object Balance : AppDestination("balance", "Balance", Icons.Filled.AccountBalance)
    data object Settings : AppDestination("settings", "Settings", Icons.Filled.Settings)
}

private val bottomDestinations = listOf(
    AppDestination.Home,
    AppDestination.Groups,
    AppDestination.History,
    AppDestination.Balance,
    AppDestination.Settings
)

@Composable
fun AppNavGraph(authRepository: AuthRepository) {
    var isLoggedIn by remember { mutableStateOf(authRepository.isLoggedIn()) }

    if (!isLoggedIn) {
        LoginScreen(onLoginSuccess = { isLoggedIn = true })
    } else {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    bottomDestinations.forEach { destination ->
                        val selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true
                        NavigationBarItem(
                            icon = { Icon(destination.icon, contentDescription = destination.label) },
                            label = { Text(destination.label) },
                            selected = selected,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            ),
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = AppDestination.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(AppDestination.Home.route) { HomeScreen() }
                composable(AppDestination.Groups.route) { GroupsScreen() }
                composable(AppDestination.History.route) { HistoryScreen() }
                composable(AppDestination.Balance.route) { BalanceScreen() }
                composable(AppDestination.Settings.route) {
                    SettingsScreen(onLogout = { isLoggedIn = false })
                }
            }
        }
    }
}
