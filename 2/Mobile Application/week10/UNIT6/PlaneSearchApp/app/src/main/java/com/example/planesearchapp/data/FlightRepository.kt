package com.example.planesearchapp.data

import kotlinx.coroutines.flow.Flow

class FlightRepository(private val airportDao: AirportDao) {
    fun getAirportsBySearch(searchQuery: String): Flow<List<Airport>> =
        airportDao.getAirportsBySearch(searchQuery)

    fun getDestinationAirports(departureCode: String): Flow<List<Airport>> =
        airportDao.getDestinationAirports(departureCode)

    suspend fun insertFavorite(departureCode: String, destinationCode: String) {
        airportDao.insertFavorite(Favorite(departureCode = departureCode, destinationCode = destinationCode))
    }

    suspend fun deleteFavorite(departureCode: String, destinationCode: String) {
        airportDao.deleteFavorite(departureCode, destinationCode)
    }

    fun getAllFavoriteRoutes(): Flow<List<FavoriteRoute>> =
        airportDao.getAllFavoriteRoutes()

    fun isFavorite(departureCode: String, destinationCode: String): Flow<Boolean> =
        airportDao.isFavorite(departureCode, destinationCode)
}
