package com.example.groupgo.data.repository

import com.example.groupgo.data.local.dao.TripRecordDao
import com.example.groupgo.data.local.mapper.TripMapper
import com.example.groupgo.domain.model.TripRecord
import com.example.groupgo.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TripRepositoryImpl @Inject constructor(
    private val tripRecordDao: TripRecordDao
) : TripRepository {

    override fun getAllTrips(): Flow<List<TripRecord>> =
        tripRecordDao.getAllTrips().map { entities ->
            entities.map(TripMapper::toDomain)
        }

    override fun getTripsByGroup(groupId: String): Flow<List<TripRecord>> =
        tripRecordDao.getTripsByGroup(groupId).map { entities ->
            entities.map(TripMapper::toDomain)
        }

    override suspend fun saveTrip(trip: TripRecord) {
        tripRecordDao.insertTrip(TripMapper.toEntity(trip))
    }

    override suspend fun deleteTrip(tripId: String) {
        tripRecordDao.getTripById(tripId)?.let { tripRecordDao.deleteTrip(it) }
    }
}
