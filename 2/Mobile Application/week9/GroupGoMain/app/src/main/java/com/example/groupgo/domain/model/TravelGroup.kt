package com.example.groupgo.domain.model

data class TravelGroup(
    val id: String,
    val name: String,
    val memberNames: List<String>,
    val createdAtEpoch: Long
)
