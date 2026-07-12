package com.example.groupgo.domain.repository

import com.example.groupgo.domain.model.TransportOption

interface RouteRepository {
    suspend fun getRouteOptions(
        originLat: Double,
        originLon: Double,
        destLat: Double,
        destLon: Double,
        originName: String = "",
        destName: String = ""
    ): Result<List<TransportOption>> // returns all modes compared
}
