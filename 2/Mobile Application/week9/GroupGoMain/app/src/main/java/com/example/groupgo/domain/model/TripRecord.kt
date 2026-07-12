package com.example.groupgo.domain.model

data class TripRecord(
    val id: String,
    val groupId: String,
    val origin: String,
    val destination: String,
    val transportMode: String,
    val totalCostPln: Double,
    val tripDateEpoch: Long, // Unix timestamp of when trip was made
    val payerName: String = ""
)
