package com.example.groupgo.data.remote

import com.example.groupgo.data.remote.dto.RouteResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface RouteApiService {
    @GET("routed-car/route/v1/driving/{coordinates}?overview=false")
    suspend fun getCarRoute(
        @Path("coordinates") coordinates: String
    ): Response<RouteResponseDto>

    @GET("routed-bike/route/v1/bicycle/{coordinates}?overview=false")
    suspend fun getBikeRoute(
        @Path("coordinates") coordinates: String
    ): Response<RouteResponseDto>

    @GET("routed-foot/route/v1/foot/{coordinates}?overview=false")
    suspend fun getFootRoute(
        @Path("coordinates") coordinates: String
    ): Response<RouteResponseDto>
}
