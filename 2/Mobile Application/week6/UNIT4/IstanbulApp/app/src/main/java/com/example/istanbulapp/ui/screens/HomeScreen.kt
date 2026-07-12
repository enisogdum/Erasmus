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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.istanbulapp.data.Category
import com.example.istanbulapp.ui.components.CategoryCard

// ─────────────────────────────────────────────
//  🏠 HOME SCREEN
//
//  This is the first screen users see.
//  It contains:
//    1. A colourful banner header with the app title
//    2. A "Did You Know?" fact card about Istanbul
//    3. A 2-column grid of 5 category buttons
//
//  📚 KEY CONCEPT — LazyVerticalGrid:
//    Like LazyColumn but arranges items in a grid.
//    "Lazy" means it only renders items that are visible on screen,
//    which keeps the app fast even with many items.
//
//    GridItemSpan(2) = this item spans BOTH columns (full width)
//    GridItemSpan(1) = this item occupies one column (default)
// ─────────────────────────────────────────────

@Composable
fun HomeScreen(
    onCategoryClick: (Category) -> Unit    // Called when user taps a category card
) {
    LazyVerticalGrid(
        columns         = GridCells.Fixed(2),        // 2 items per row
        modifier        = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding  = PaddingValues(bottom = 16.dp),
        verticalArrangement   = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // ── HEADER BANNER (spans full width) ──────────────────────────
        item(span = { GridItemSpan(maxLineSpan) }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text       = "🕌 Istanbul Guide",
                        fontSize   = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text  = "Discover the magic of Istanbul",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }
        }

        // ── DID YOU KNOW CARD (spans full width) ───────────────────────
        item(span = { GridItemSpan(maxLineSpan) }) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text       = "✨ Did You Know?",
                        style      = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text  = "Istanbul is the only city in the world built on two continents — Europe and Asia — divided by the Bosphorus Strait.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
                    )
                }
            }
        }

        // ── SECTION TITLE (spans full width) ──────────────────────────
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text       = "Explore by Category",
                style      = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier   = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }

        // ── CATEGORY CARDS (2 per row — each spans 1 column) ──────────
        // Category.entries gives all 5 enum values as a list
        items(
            items = Category.entries,
            key   = { it.name }          // Stable key helps Compose with animations
        ) { category ->
            CategoryCard(
                category = category,
                onClick  = { onCategoryClick(category) },
                modifier = Modifier.padding(
                    start = if (Category.entries.indexOf(category) % 2 == 0) 16.dp else 0.dp,
                    end   = if (Category.entries.indexOf(category) % 2 == 1) 16.dp else 0.dp
                )
            )
        }

        // Extra bottom spacing so last row isn't hidden by the nav bar
        item(span = { GridItemSpan(maxLineSpan) }) {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
