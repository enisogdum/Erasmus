package com.example.groupgo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trip_records")
data class TripRecordEntity(
    @PrimaryKey val id: String,
    val groupId: String,
    val origin: String,
    val destination: String,
    val transportMode: String,
    val totalCostPln: Double,
    val tripDateEpoch: Long,
    val payerName: String = ""
)
