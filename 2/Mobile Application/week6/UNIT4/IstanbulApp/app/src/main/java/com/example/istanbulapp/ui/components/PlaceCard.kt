package com.example.istanbulapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.istanbulapp.data.Place

// ─────────────────────────────────────────────
//  🃏 PLACE CARD — Reusable Component
//
//  This is a "reusable composable" — we define it ONCE and
//  use it on both the CategoryScreen and ExploreScreen.
//
//  📚 KEY CONCEPT — Composable functions:
//    A @Composable function describes part of the UI.
//    It can receive data (parameters) and display it.
//    When the data changes, Compose automatically redraws the UI.
//
//  PARAMETERS:
//    place    — the Place object to display
//    onClick  — what happens when the card is tapped
//    modifier — lets callers customize size, padding, etc.
// ─────────────────────────────────────────────

@Composable
fun PlaceCard(
    place: Place,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },      // Makes the entire card tappable
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── Left: Category Color Box (acts as thumbnail) ───────────
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(place.category.color)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text     = place.category.emoji,
                    fontSize = 30.sp
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // ── Right: Place Info ─────────────────────────────────────
            Column(modifier = Modifier.weight(1f)) {   // weight(1f) = take remaining space

                // Place name (bold)
                Text(
                    text      = place.name,
                    style     = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines  = 1,
                    overflow  = TextOverflow.Ellipsis   // "..." if text is too long
                )

                Spacer(modifier = Modifier.height(3.dp))

                // Short description
                Text(
                    text     = place.description,
                    style    = MaterialTheme.typography.bodySmall,
                    color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Location + Rating row
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 📍 Location
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector     = Icons.Filled.LocationOn,
                            contentDescription = "Location",
                            tint            = MaterialTheme.colorScheme.primary,
                            modifier        = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text  = place.location,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // ⭐ Rating
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector     = Icons.Filled.Star,
                            contentDescription = "Rating",
                            tint            = Color(0xFFFFB300),   // Amber star color
                            modifier        = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text       = place.rating.toString(),
                            style      = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
