package com.example.groupgo.data.remote.mapper

import com.example.groupgo.data.remote.dto.RouteResponseDto
import com.example.groupgo.domain.model.TransportOption
import javax.inject.Inject

class RouteMapper @Inject constructor() {

    fun toTransportOptions(
        dto: RouteResponseDto,
        mode: String,
        costPln: Double,
        timeValueScore: Double
    ): List<TransportOption> {
        return dto.routes.map { route ->
            TransportOption(
                mode = mode,
                durationMinutes = (route.durationSeconds / 60).toInt(),
                costPln = costPln,
                distanceKm = route.distanceMeters / 1000.0,
                timeValueScore = timeValueScore
            )
        }
    }
}
