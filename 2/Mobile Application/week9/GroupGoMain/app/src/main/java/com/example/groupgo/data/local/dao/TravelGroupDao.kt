package com.example.groupgo.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.groupgo.data.local.entity.TravelGroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TravelGroupDao {
    @Query("SELECT * FROM travel_groups ORDER BY createdAtEpoch DESC")
    fun getAllGroups(): Flow<List<TravelGroupEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(entity: TravelGroupEntity)

    @Delete
    suspend fun deleteGroup(entity: TravelGroupEntity)

    @Query("SELECT * FROM travel_groups WHERE id = :groupId")
    suspend fun getGroupById(groupId: String): TravelGroupEntity?
}
