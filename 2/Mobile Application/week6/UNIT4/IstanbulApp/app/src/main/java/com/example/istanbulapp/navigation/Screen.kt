package com.example.istanbulapp.navigation

// ─────────────────────────────────────────────
//  🧭 NAVIGATION ROUTES
//
//  In Jetpack Navigation, every screen has a "route" —
//  a unique string that acts like an address/URL for the screen.
//
//  📚 KEY CONCEPT — sealed class:
//    A sealed class is like an enum, but each entry can be
//    a different type and hold different data. We use it here
//    so that every possible screen is defined in one place,
//    making it easy to see the full navigation structure.
//
//  ROUTE EXAMPLES:
//    "home"                  → Home screen (no arguments)
//    "category/PARKS"        → Parks category list
//    "detail/7"              → Detail page for place with id=7
// ─────────────────────────────────────────────

sealed class Screen(val route: String) {

    /** 🏠 Home screen — the first screen the user sees */
    object Home : Screen("home")

    /** 🔍 Explore screen — shows ALL places in a searchable list */
    object Explore : Screen("explore")

    /** ℹ️ About screen — interesting facts about Istanbul */
    object About : Screen("about")

    /**
     * 📋 Category screen — shows places filtered by category.
     *
     * The route contains `{categoryName}` as a placeholder.
     * When navigating, we replace it with the actual category name,
     * e.g. "category/PARKS" or "category/COFFEE_SHOPS".
     */
    object CategoryScreen : Screen("category/{categoryName}") {
        /** Call this to build the real route before navigating */
        fun createRoute(categoryName: String) = "category/$categoryName"
    }

    /**
     * 📍 Place Detail screen — shows full info for one place.
     *
     * The route contains `{placeId}` as a placeholder.
     * e.g. "detail/3" shows the place with id=3.
     */
    object PlaceDetail : Screen("detail/{placeId}") {
        /** Call this to build the real route before navigating */
        fun createRoute(placeId: Int) = "detail/$placeId"
    }
}
