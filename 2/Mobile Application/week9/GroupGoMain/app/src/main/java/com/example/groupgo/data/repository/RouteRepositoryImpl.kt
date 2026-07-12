package com.example.groupgo.data.repository

import com.example.groupgo.data.remote.RouteApiService
import com.example.groupgo.data.remote.mapper.RouteMapper
import com.example.groupgo.domain.model.TransportOption
import com.example.groupgo.domain.repository.RouteRepository
import com.example.groupgo.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class RouteRepositoryImpl @Inject constructor(
    private val routeApiService: RouteApiService,
    private val routeMapper: RouteMapper,
    private val settingsRepository: SettingsRepository
) : RouteRepository {

    override suspend fun getRouteOptions(
        originLat: Double,
        originLon: Double,
        destLat: Double,
        destLon: Double,
        originName: String,
        destName: String
    ): Result<List<TransportOption>> {
        return try {
            val settings = settingsRepository.getSettings().first()
            val hourlyRate = if (settings.avgWorkingHoursPerMonth > 0) {
                settings.monthlySalaryPln / settings.avgWorkingHoursPerMonth
            } else 0.0

            val coordinates = "$originLon,$originLat;$destLon,$destLat"
            val options = mutableListOf<TransportOption>()

            // Get pricing parameters for the detected country
            val pricing = getPricingForCountry(originName, destName)

            // 1. Fetch car route
            val carResponse = routeApiService.getCarRoute(coordinates)
            if (carResponse.isSuccessful) {
                carResponse.body()?.let { body ->
                    val baseOptions = routeMapper.toTransportOptions(body, "car", 0.0, 0.0)
                    options += baseOptions.map { option ->
                        val cost = option.distanceKm * pricing.carCostPerKmPln
                        val timeValue = (option.durationMinutes / 60.0) * hourlyRate
                        option.copy(
                            costPln = cost,
                            timeValueScore = timeValue
                        )
                    }

                    // Dynamically simulate Transit, Taxi, and Flight based on the car route distance & duration
                    if (baseOptions.isNotEmpty()) {
                        val baseOption = baseOptions[0]
                        val dist = baseOption.distanceKm
                        val dur = baseOption.durationMinutes

                        // A. Simulate Public Transit (Bus/Train)
                        val transitDur = (dur * 1.35).toInt() + 12
                        val transitCost = pricing.transitBaseCostPln + (dist * pricing.transitCostPerKmPln)
                        val transitTimeValue = (transitDur / 60.0) * hourlyRate
                        options += TransportOption(
                            mode = "transit",
                            durationMinutes = transitDur,
                            costPln = transitCost,
                            distanceKm = dist,
                            timeValueScore = transitTimeValue
                        )

                        // B. Simulate Taxi/Ride-share
                        val taxiCost = pricing.taxiBaseCostPln + (dist * pricing.taxiCostPerKmPln)
                        val taxiTimeValue = (dur / 60.0) * hourlyRate
                        options += TransportOption(
                            mode = "taxi",
                            durationMinutes = dur,
                            costPln = taxiCost,
                            distanceKm = dist,
                            timeValueScore = taxiTimeValue
                        )

                        // C. Simulate Flight (only for long trips > 400 km)
                        if (dist >= 400.0) {
                            val flightDur = ((dist / 700.0) * 60).toInt() + 110
                            val flightCost = 180.0 + (dist * 0.12 * 4.3) // 180 PLN base + ~0.50 PLN/km
                            val flightTimeValue = (flightDur / 60.0) * hourlyRate
                            options += TransportOption(
                                mode = "flight",
                                durationMinutes = flightDur,
                                costPln = flightCost,
                                distanceKm = dist,
                                timeValueScore = flightTimeValue
                            )
                        }
                    }
                }
            }

            // 2. Fetch bike route
            val bikeResponse = routeApiService.getBikeRoute(coordinates)
            if (bikeResponse.isSuccessful) {
                bikeResponse.body()?.let { body ->
                    val baseOptions = routeMapper.toTransportOptions(body, "bike", 0.0, 0.0)
                    options += baseOptions.map { option ->
                        val timeValue = (option.durationMinutes / 60.0) * hourlyRate
                        option.copy(
                            costPln = 0.0,
                            timeValueScore = timeValue
                        )
                    }
                }
            }

            // 3. Fetch foot route
            val footResponse = routeApiService.getFootRoute(coordinates)
            if (footResponse.isSuccessful) {
                footResponse.body()?.let { body ->
                    val baseOptions = routeMapper.toTransportOptions(body, "walk", 0.0, 0.0)
                    options += baseOptions.map { option ->
                        val timeValue = (option.durationMinutes / 60.0) * hourlyRate
                        option.copy(
                            costPln = 0.0,
                            timeValueScore = timeValue
                        )
                    }
                }
            }

            Result.success(options)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

private data class CountryPricing(
    val carCostPerKmPln: Double,
    val transitBaseCostPln: Double,
    val transitCostPerKmPln: Double,
    val taxiBaseCostPln: Double,
    val taxiCostPerKmPln: Double
)

private val countryPricings = mapOf(
    "spain" to CountryPricing(
        carCostPerKmPln = 0.22 * 4.3,
        transitBaseCostPln = 2.40 * 4.3,
        transitCostPerKmPln = 0.05 * 4.3,
        taxiBaseCostPln = 3.50 * 4.3,
        taxiCostPerKmPln = 1.20 * 4.3
    ),
    "germany" to CountryPricing(
        carCostPerKmPln = 0.24 * 4.3,
        transitBaseCostPln = 3.00 * 4.3,
        transitCostPerKmPln = 0.06 * 4.3,
        taxiBaseCostPln = 4.00 * 4.3,
        taxiCostPerKmPln = 2.00 * 4.3
    ),
    "france" to CountryPricing(
        carCostPerKmPln = 0.25 * 4.3,
        transitBaseCostPln = 2.10 * 4.3,
        transitCostPerKmPln = 0.07 * 4.3,
        taxiBaseCostPln = 4.00 * 4.3,
        taxiCostPerKmPln = 1.80 * 4.3
    ),
    "united kingdom" to CountryPricing(
        carCostPerKmPln = 0.20 * 5.0,
        transitBaseCostPln = 2.80 * 5.0,
        transitCostPerKmPln = 0.08 * 5.0,
        taxiBaseCostPln = 3.20 * 5.0,
        taxiCostPerKmPln = 1.50 * 5.0
    ),
    "uk" to CountryPricing(
        carCostPerKmPln = 0.20 * 5.0,
        transitBaseCostPln = 2.80 * 5.0,
        transitCostPerKmPln = 0.08 * 5.0,
        taxiBaseCostPln = 3.20 * 5.0,
        taxiCostPerKmPln = 1.50 * 5.0
    ),
    "united states" to CountryPricing(
        carCostPerKmPln = 0.35 * 4.0,
        transitBaseCostPln = 2.75 * 4.0,
        transitCostPerKmPln = 0.10 * 4.0,
        taxiBaseCostPln = 3.00 * 4.0,
        taxiCostPerKmPln = 2.50 * 4.0
    ),
    "us" to CountryPricing(
        carCostPerKmPln = 0.35 * 4.0,
        transitBaseCostPln = 2.75 * 4.0,
        transitCostPerKmPln = 0.10 * 4.0,
        taxiBaseCostPln = 3.00 * 4.0,
        taxiCostPerKmPln = 2.50 * 4.0
    ),
    "poland" to CountryPricing(
        carCostPerKmPln = 0.90,
        transitBaseCostPln = 4.40,
        transitCostPerKmPln = 0.20,
        taxiBaseCostPln = 8.00,
        taxiCostPerKmPln = 3.00
    )
)

private fun getPricingForCountry(origin: String, dest: String): CountryPricing {
    val searchStr = (origin + " " + dest).lowercase()
    for ((key, pricing) in countryPricings) {
        if (searchStr.contains(key)) return pricing
    }
    return countryPricings["poland"]!!
}
