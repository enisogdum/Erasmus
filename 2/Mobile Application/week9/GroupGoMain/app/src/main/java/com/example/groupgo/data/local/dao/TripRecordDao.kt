package com.example.groupgo.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.groupgo.data.local.entity.TripRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripRecordDao {
    @Query("SELECT * FROM trip_records ORDER BY tripDateEpoch DESC")
    fun getAllTrips(): Flow<List<TripRecordEntity>>

    @Query(
        "SELECT * FROM trip_records WHERE groupId = :groupId " +
            "ORDER BY tripDateEpoch DESC"
    )
    fun getTripsByGroup(groupId: String): Flow<List<TripRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(entity: TripRecordEntity)

    @Delete
    suspend fun deleteTrip(entity: TripRecordEntity)

    @Query("SELECT * FROM trip_records WHERE id = :tripId")
    suspend fun getTripById(tripId: String): TripRecordEntity?
}
