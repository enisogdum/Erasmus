package com.example.groupgo.domain.repository

import com.example.groupgo.domain.model.TravelGroup
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    fun getAllGroups(): Flow<List<TravelGroup>>
    suspend fun createGroup(group: TravelGroup)
    suspend fun deleteGroup(groupId: String)
}
