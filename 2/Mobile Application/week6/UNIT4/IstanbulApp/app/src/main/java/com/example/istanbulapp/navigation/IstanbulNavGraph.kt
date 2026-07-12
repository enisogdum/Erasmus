package com.example.istanbulapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.istanbulapp.data.Category
import com.example.istanbulapp.ui.screens.AboutScreen
import com.example.istanbulapp.ui.screens.CategoryScreen
import com.example.istanbulapp.ui.screens.ExploreScreen
import com.example.istanbulapp.ui.screens.HomeScreen
import com.example.istanbulapp.ui.screens.PlaceDetailScreen

// ─────────────────────────────────────────────
//  🗺️ NAVIGATION GRAPH
//
//  NavHost is the container that holds all our screens.
//  It knows which screen to show based on the current route.
//
//  Think of it like a router in a web app:
//    URL "/home"        → shows HomeScreen
//    URL "/explore"     → shows ExploreScreen
//    URL "/category/PARKS" → shows CategoryScreen for Parks
//    URL "/detail/7"    → shows PlaceDetailScreen for place #7
//
//  HOW NAVIGATION WORKS:
//    1. User taps a button
//    2. We call navController.navigate("some/route")
//    3. NavHost matches the route and shows the right screen
//    4. To go back: navController.popBackStack()
// ─────────────────────────────────────────────

@Composable
fun IstanbulNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController    = navController,
        startDestination = Screen.Home.route,   // First screen shown on launch
        modifier         = modifier
    ) {

        // ── HOME ─────────────────────────────────────────────────────────
        composable(Screen.Home.route) {
            HomeScreen(
                onCategoryClick = { category ->
                    // Navigate to the CategoryScreen, passing the category name as a string
                    navController.navigate(
                        Screen.CategoryScreen.createRoute(category.name)
                    )
                }
            )
        }

        // ── EXPLORE ───────────────────────────────────────────────────────
        composable(Screen.Explore.route) {
            ExploreScreen(
                onPlaceClick = { placeId ->
                    navController.navigate(Screen.PlaceDetail.createRoute(placeId))
                }
            )
        }

        // ── ABOUT ─────────────────────────────────────────────────────────
        composable(Screen.About.route) {
            AboutScreen()
        }

        // ── CATEGORY ──────────────────────────────────────────────────────
        // This route receives a "categoryName" string argument
        composable(
            route     = Screen.CategoryScreen.route,
            arguments = listOf(
                navArgument("categoryName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // Extract the argument from the navigation back stack entry
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
            // Convert the string "PARKS" back into the Category.PARKS enum value
            val category = Category.valueOf(categoryName)

            CategoryScreen(
                category    = category,
                onPlaceClick = { placeId ->
                    navController.navigate(Screen.PlaceDetail.createRoute(placeId))
                },
                onBackClick = {
                    navController.popBackStack()   // ← Go back to previous screen
                }
            )
        }

        // ── PLACE DETAIL ──────────────────────────────────────────────────
        // This route receives a "placeId" integer argument
        composable(
            route     = Screen.PlaceDetail.route,
            arguments = listOf(
                navArgument("placeId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val placeId = backStackEntry.arguments?.getInt("placeId") ?: 0

            PlaceDetailScreen(
                placeId     = placeId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
