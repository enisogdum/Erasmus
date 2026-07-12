package com.example.groupgo.domain.model

data class TransportOption(
    val mode: String, // e.g. "bus", "car", "walk"
    val durationMinutes: Int,
    val costPln: Double,
    val distanceKm: Double,
    val timeValueScore: Double // computed from user's salary settings
)
