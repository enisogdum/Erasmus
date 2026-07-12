package com.example.groupgo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RouteResponseDto(
    @SerializedName("routes") val routes: List<RouteDto>
)

data class RouteDto(
    @SerializedName("distance") val distanceMeters: Double,
    @SerializedName("duration") val durationSeconds: Double
)
