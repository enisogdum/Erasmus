package com.example.istanbulapp.data

// ─────────────────────────────────────────────
//  📦 DATA MODEL
//
//  This file defines:
//    1. Category  — an enum of the 5 place categories
//    2. Place     — a data class holding all info about a place
//
//  📚 KEY CONCEPTS:
//
//  enum class:
//    A fixed, named list of constants. Great for categories
//    that never change. Each entry can have its own properties.
//
//  data class:
//    Kotlin's way of creating simple objects that hold data.
//    It automatically gives you equals(), hashCode(), toString(),
//    and copy() methods for free.
// ─────────────────────────────────────────────

/**
 * Represents the 5 categories of places in the app.
 *
 * @param displayName  Human-readable name shown in the UI
 * @param emoji        Emoji icon shown in the category card
 * @param color        Background color for this category (as a Long hex value)
 */
enum class Category(
    val displayName: String,
    val emoji: String,
    val color: Long
) {
    COFFEE_SHOPS(
        displayName = "Coffee Shops",
        emoji       = "☕",
        color       = 0xFF6F4E37   // Rich coffee brown
    ),
    RESTAURANTS(
        displayName = "Restaurants",
        emoji       = "🍽️",
        color       = 0xFFC1440E   // Turkish red
    ),
    PARKS(
        displayName = "Parks",
        emoji       = "🌿",
        color       = 0xFF2E7D32   // Nature green
    ),
    SHOPPING(
        displayName = "Shopping",
        emoji       = "🛍️",
        color       = 0xFF7B1FA2   // Deep purple
    ),
    KID_FRIENDLY(
        displayName = "Kid-Friendly",
        emoji       = "🎠",
        color       = 0xFF1565C0   // Playful blue
    )
}

/**
 * Represents a single place recommendation in Istanbul.
 *
 * @param id              Unique number that identifies this place
 * @param name            The place's name
 * @param description     Short 1-sentence teaser shown on cards
 * @param fullDescription Long paragraph shown on the detail screen
 * @param location        Neighborhood / district (e.g. "Kadıköy")
 * @param address         Full street address
 * @param category        Which [Category] this place belongs to
 * @param rating          Rating out of 5.0 stars
 * @param openingHours    When the place is open
 */
data class Place(
    val id: Int,
    val name: String,
    val description: String,
    val fullDescription: String,
    val location: String,
    val address: String,
    val category: Category,
    val rating: Float,
    val openingHours: String
)
