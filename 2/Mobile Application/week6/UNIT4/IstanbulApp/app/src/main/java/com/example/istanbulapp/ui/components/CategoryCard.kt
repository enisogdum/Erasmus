package com.example.istanbulapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.istanbulapp.data.Category
import com.example.istanbulapp.data.SampleData

// ─────────────────────────────────────────────
//  🗂️ CATEGORY CARD — Big Tappable Button
//
//  Displayed in the 2-column grid on the Home screen.
//  Each card shows an emoji, category name, and place count.
//
//  The card fills its grid cell with a colored gradient
//  background unique to each category.
// ─────────────────────────────────────────────

@Composable
fun CategoryCard(
    category: Category,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Count how many places belong to this category
    val placeCount = SampleData.getPlacesByCategory(category).size

    // Convert the Long hex value to a Compose Color
    val baseColor = Color(category.color)

    Card(
        modifier  = modifier
            .aspectRatio(0.95f)              // Slightly taller than wide — looks better in grids
            .clickable { onClick() },
        shape     = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        // Box fills the card and applies a gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    // Vertical gradient: lighter at top, deeper at bottom
                    Brush.verticalGradient(
                        colors = listOf(
                            baseColor.copy(alpha = 0.75f),
                            baseColor
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier            = Modifier.padding(12.dp)
            ) {
                // Large emoji icon
                Text(
                    text     = category.emoji,
                    fontSize = 44.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Category name in bold white
                Text(
                    text      = category.displayName,
                    style     = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color     = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Small pill badge showing number of places
                Surface(
                    shape = RoundedCornerShape(50.dp),     // 50dp makes it fully rounded (pill shape)
                    color = Color.White.copy(alpha = 0.25f) // Semi-transparent white
                ) {
                    Text(
                        text     = "$placeCount places",
                        style    = MaterialTheme.typography.labelSmall,
                        color    = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}
