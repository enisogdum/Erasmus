package com.example.istanbulapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─────────────────────────────────────────────
//  ℹ️ ABOUT SCREEN
//
//  Shows some interesting quick details and history about
//  Istanbul. Uses verticalScroll so all text content can be
//  easily read on smaller screens.
// ─────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── TOP APP BAR ────────────────────────────────────────────────
        TopAppBar(
            title = {
                Text(
                    text       = "🕌 About Istanbul",
                    fontWeight = FontWeight.Bold
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor    = MaterialTheme.colorScheme.primary,
                titleContentColor = androidx.compose.ui.graphics.Color.White
            )
        )

        // ── MAIN CONTENT ───────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Welcome to the Istanbul City Guide!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Istanbul, historically known as Byzantium and Constantinople, is the most populous city in Turkey and the country's economic, cultural, and historic hub. Istanbul is a transcontinental city in Eurasia, straddling the Bosphorus strait (which separates Europe and Asia) between the Sea of Marmara and the Black Sea.",
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp
            )

            // Fact 1 Card
            AboutFactCard(
                title = "🌍 Transcontinental Location",
                description = "It is the only metropolis in the world to sit on two continents, bridging Europe and Asia together."
            )

            // Fact 2 Card
            AboutFactCard(
                title = "🏛️ Historic Empires",
                description = "Istanbul served as the capital of four historic empires: the Roman Empire, the Byzantine Empire, the Latin Empire, and the Ottoman Empire."
            )

            // Fact 3 Card
            AboutFactCard(
                title = "☕ Coffee & Culinary Culture",
                description = "Turkish coffee was introduced to the world here in 1555. The city remains a global capital of food, culture, and rich Anatolian flavors."
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun AboutFactCard(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}
