package com.example.planesearchapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportDao {
    @Query("SELECT * FROM airport WHERE iata_code LIKE '%' || :searchQuery || '%' OR name LIKE '%' || :searchQuery || '%' ORDER BY passengers DESC")
    fun getAirportsBySearch(searchQuery: String): Flow<List<Airport>>

    @Query("SELECT * FROM airport WHERE iata_code != :departureCode ORDER BY passengers DESC")
    fun getDestinationAirports(departureCode: String): Flow<List<Airport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: Favorite)

    @Query("DELETE FROM favorite WHERE departure_code = :departureCode AND destination_code = :destinationCode")
    suspend fun deleteFavorite(departureCode: String, destinationCode: String)

    @Query("""
        SELECT 
            f.id AS id,
            f.departure_code AS departureCode,
            dep.name AS departureName,
            f.destination_code AS destinationCode,
            dest.name AS destinationName
        FROM favorite f
        INNER JOIN airport dep ON f.departure_code = dep.iata_code
        INNER JOIN airport dest ON f.destination_code = dest.iata_code
    """)
    fun getAllFavoriteRoutes(): Flow<List<FavoriteRoute>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite WHERE departure_code = :departureCode AND destination_code = :destinationCode)")
    fun isFavorite(departureCode: String, destinationCode: String): Flow<Boolean>
}
