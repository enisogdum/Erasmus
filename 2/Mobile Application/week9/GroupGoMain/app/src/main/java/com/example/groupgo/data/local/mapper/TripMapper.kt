package com.example.groupgo.data.local.mapper

import com.example.groupgo.data.local.entity.TripRecordEntity
import com.example.groupgo.domain.model.TripRecord

object TripMapper {

    fun toDomain(entity: TripRecordEntity): TripRecord = TripRecord(
        id = entity.id,
        groupId = entity.groupId,
        origin = entity.origin,
        destination = entity.destination,
        transportMode = entity.transportMode,
        totalCostPln = entity.totalCostPln,
        tripDateEpoch = entity.tripDateEpoch,
        payerName = entity.payerName
    )

    fun toEntity(trip: TripRecord): TripRecordEntity = TripRecordEntity(
        id = trip.id,
        groupId = trip.groupId,
        origin = trip.origin,
        destination = trip.destination,
        transportMode = trip.transportMode,
        totalCostPln = trip.totalCostPln,
        tripDateEpoch = trip.tripDateEpoch,
        payerName = trip.payerName
    )
}
