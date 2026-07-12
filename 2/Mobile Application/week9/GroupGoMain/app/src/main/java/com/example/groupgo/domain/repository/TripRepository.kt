package com.example.groupgo.domain.repository

import com.example.groupgo.domain.model.TripRecord
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    fun getAllTrips(): Flow<List<TripRecord>>
    fun getTripsByGroup(groupId: String): Flow<List<TripRecord>>
    suspend fun saveTrip(trip: TripRecord)
    suspend fun deleteTrip(tripId: String)
}
