package com.example.groupgo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "travel_groups")
data class TravelGroupEntity(
    @PrimaryKey val id: String,
    val name: String,
    val memberNames: String, // stored as JSON string
    val createdAtEpoch: Long
)
