package com.example.istanbulapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.istanbulapp.data.Category
import com.example.istanbulapp.data.SampleData
import com.example.istanbulapp.ui.components.PlaceCard

// ─────────────────────────────────────────────
//  📋 CATEGORY SCREEN
//
//  Shown when a user taps a category card on the Home screen.
//  Displays a large top bar with the category name,
//  then lists all places belonging to that category.
//
//  📚 KEY CONCEPTS:
//
//  Scaffold:
//    A layout template that provides pre-built slots for:
//      topBar, bottomBar, floatingActionButton, content
//    We use it here to add a LargeTopAppBar with a back button.
//
//  LazyColumn:
//    Like RecyclerView in the old View system.
//    Renders only the items currently visible on screen.
//    Great for long, scrollable lists.
//
//  LargeTopAppBar:
//    A Material 3 top bar that collapses when you scroll down.
// ─────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    category: Category,
    onPlaceClick: (Int) -> Unit,     // Called with placeId when user taps a PlaceCard
    onBackClick: () -> Unit          // Called when user taps the back arrow
) {
    // Get only the places that belong to this category
    val places = SampleData.getPlacesByCategory(category)
    val categoryColor = Color(category.color)

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            text      = "${category.emoji}  ${category.displayName}",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text  = "${places.size} places in Istanbul",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f)
                        )
                    }
                },
                navigationIcon = {
                    // Back button — tapping calls onBackClick → navController.popBackStack()
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back",
                            tint               = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor        = categoryColor,
                    scrolledContainerColor = categoryColor.copy(alpha = 0.9f),
                    titleContentColor     = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        // innerPadding accounts for the top app bar height

        if (places.isEmpty()) {
            // Empty state — shown if no places are found (shouldn't happen with our data)
            Box(
                modifier          = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment  = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "🔍", fontSize = 48.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text  = "No places found",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier        = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background),
                contentPadding  = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = places,
                    key   = { it.id }   // Unique key for each item (good practice)
                ) { place ->
                    PlaceCard(
                        place   = place,
                        onClick = { onPlaceClick(place.id) }
                    )
                }
            }
        }
    }
}
