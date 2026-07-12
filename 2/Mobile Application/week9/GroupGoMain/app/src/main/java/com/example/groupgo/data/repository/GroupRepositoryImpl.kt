package com.example.groupgo.data.repository

import com.example.groupgo.data.local.dao.TravelGroupDao
import com.example.groupgo.data.local.mapper.GroupMapper
import com.example.groupgo.domain.model.TravelGroup
import com.example.groupgo.domain.repository.GroupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(
    private val travelGroupDao: TravelGroupDao
) : GroupRepository {

    override fun getAllGroups(): Flow<List<TravelGroup>> =
        travelGroupDao.getAllGroups().map { entities ->
            entities.map(GroupMapper::toDomain)
        }

    override suspend fun createGroup(group: TravelGroup) {
        travelGroupDao.insertGroup(GroupMapper.toEntity(group))
    }

    override suspend fun deleteGroup(groupId: String) {
        travelGroupDao.getGroupById(groupId)?.let { travelGroupDao.deleteGroup(it) }
    }
}
