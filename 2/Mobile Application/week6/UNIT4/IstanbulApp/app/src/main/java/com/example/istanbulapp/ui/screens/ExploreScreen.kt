package com.example.istanbulapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.istanbulapp.data.SampleData
import com.example.istanbulapp.ui.components.PlaceCard

// ─────────────────────────────────────────────
//  🔍 EXPLORE SCREEN
//
//  Shows ALL 15 places in a scrollable list.
//  Also includes a live search bar that filters
//  results as the user types.
//
//  📚 KEY CONCEPTS:
//
//  State (remember + mutableStateOf):
//    State is data that can CHANGE during the app's lifetime.
//    When state changes, Compose automatically redraws the UI.
//
//    `remember { mutableStateOf("") }` creates a String state
//    that starts empty and persists across recompositions.
//
//    `by` is a Kotlin delegate — it lets us write
//      searchQuery  instead of  searchQuery.value
//
//  Live filtering:
//    We use `.filter { }` every time searchQuery changes.
//    Compose re-runs the composable, the filter runs again,
//    and LazyColumn updates automatically.
// ─────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    onPlaceClick: (Int) -> Unit
) {
    // State: the current text in the search bar
    // `by` = Kotlin property delegation — lets us use searchQuery directly
    var searchQuery by remember { mutableStateOf("") }

    // Filter the full place list based on searchQuery
    // `.lowercase()` makes the search case-insensitive
    val filteredPlaces = SampleData.places.filter { place ->
        searchQuery.isBlank() ||                                // Show all if search is empty
        place.name.lowercase().contains(searchQuery.lowercase()) ||
        place.location.lowercase().contains(searchQuery.lowercase()) ||
        place.category.displayName.lowercase().contains(searchQuery.lowercase())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── TOP APP BAR ────────────────────────────────────────────────
        TopAppBar(
            title = {
                Text(
                    text       = "🔍 Explore Istanbul",
                    fontWeight = FontWeight.Bold
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor    = MaterialTheme.colorScheme.primary,
                titleContentColor = androidx.compose.ui.graphics.Color.White
            )
        )

        // ── SEARCH BAR ─────────────────────────────────────────────────
        OutlinedTextField(
            value         = searchQuery,
            onValueChange = { searchQuery = it },   // Update state as user types
            placeholder   = { Text("Search places, areas...") },
            leadingIcon   = {
                Icon(
                    imageVector        = Icons.Filled.Search,
                    contentDescription = "Search",
                    tint               = MaterialTheme.colorScheme.primary
                )
            },
            singleLine = true,
            shape      = RoundedCornerShape(12.dp),
            modifier   = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )

        // ── RESULTS COUNT ──────────────────────────────────────────────
        Text(
            text     = "${filteredPlaces.size} places found",
            style    = MaterialTheme.typography.labelMedium,
            color    = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
            modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
        )

        // ── PLACE LIST ─────────────────────────────────────────────────
        LazyColumn(
            modifier       = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = filteredPlaces,
                key   = { it.id }
            ) { place ->
                PlaceCard(
                    place   = place,
                    onClick = { onPlaceClick(place.id) }
                )
            }
        }
    }
}
