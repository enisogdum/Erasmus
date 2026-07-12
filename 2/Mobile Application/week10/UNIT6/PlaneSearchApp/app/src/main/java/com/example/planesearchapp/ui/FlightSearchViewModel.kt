package com.example.planesearchapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.planesearchapp.data.Airport
import com.example.planesearchapp.data.FavoriteRoute
import com.example.planesearchapp.data.FlightRepository
import com.example.planesearchapp.data.UserPreferencesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FlightSearchViewModel(
    private val flightRepository: FlightRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val searchQuery: StateFlow<String> = userPreferencesRepository.searchQuery
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchResults: StateFlow<List<Airport>> = searchQuery.flatMapLatest { query ->
        if (query.isBlank()) {
            flowOf(emptyList())
        } else {
            flightRepository.getAirportsBySearch(query)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val favoriteRoutes: StateFlow<List<FavoriteRoute>> = flightRepository.getAllFavoriteRoutes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedAirport = kotlinx.coroutines.flow.MutableStateFlow<Airport?>(null)
    val selectedAirport: StateFlow<Airport?> = _selectedAirport

    @OptIn(ExperimentalCoroutinesApi::class)
    val destinationAirports: StateFlow<List<Airport>> = _selectedAirport.flatMapLatest { airport ->
        if (airport != null) {
            flightRepository.getDestinationAirports(airport.iataCode)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun updateSearchQuery(query: String) {
        viewModelScope.launch {
            userPreferencesRepository.saveSearchQuery(query)
            if (query.isBlank()) {
                _selectedAirport.value = null
            }
        }
    }

    fun selectAirport(airport: Airport) {
        _selectedAirport.value = airport
    }

    fun clearSelectedAirport() {
        _selectedAirport.value = null
    }

    fun toggleFavorite(departureCode: String, destinationCode: String, isFavorite: Boolean) {
        viewModelScope.launch {
            if (isFavorite) {
                flightRepository.deleteFavorite(departureCode, destinationCode)
            } else {
                flightRepository.insertFavorite(departureCode, destinationCode)
            }
        }
    }
}

class FlightSearchViewModelFactory(
    private val flightRepository: FlightRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlightSearchViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FlightSearchViewModel(flightRepository, userPreferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
