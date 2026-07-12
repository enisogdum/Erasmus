package com.example.groupgo.data.local.mapper

import com.example.groupgo.data.local.entity.TravelGroupEntity
import com.example.groupgo.domain.model.TravelGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object GroupMapper {

    private val gson = Gson()
    private val memberListType = object : TypeToken<List<String>>() {}.type

    fun toDomain(entity: TravelGroupEntity): TravelGroup = TravelGroup(
        id = entity.id,
        name = entity.name,
        memberNames = gson.fromJson(entity.memberNames, memberListType) ?: emptyList(),
        createdAtEpoch = entity.createdAtEpoch
    )

    fun toEntity(group: TravelGroup): TravelGroupEntity = TravelGroupEntity(
        id = group.id,
        name = group.name,
        memberNames = gson.toJson(group.memberNames),
        createdAtEpoch = group.createdAtEpoch
    )
}
