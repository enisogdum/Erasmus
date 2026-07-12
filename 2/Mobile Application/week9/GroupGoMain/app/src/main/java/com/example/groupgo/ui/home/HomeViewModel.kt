package com.example.groupgo.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groupgo.domain.model.TripRecord
import com.example.groupgo.domain.model.TransportOption
import com.example.groupgo.domain.repository.ExpenseRepository
import com.example.groupgo.domain.repository.GroupRepository
import com.example.groupgo.domain.repository.RouteRepository
import com.example.groupgo.domain.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val routeRepository: RouteRepository,
    private val groupRepository: GroupRepository,
    private val tripRepository: TripRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            groupRepository.getAllGroups().collect { groups ->
                _uiState.update { it.copy(groups = groups) }
            }
        }
    }

    fun selectGroup(groupId: String) {
        _uiState.update { it.copy(selectedGroupId = groupId) }
    }

    fun searchRoute(originLat: Double, originLon: Double, destLat: Double, destLon: Double, originName: String = "", destName: String = "") {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            routeRepository.getRouteOptions(originLat, originLon, destLat, destLon, originName, destName)
                .onSuccess { options ->
                    _uiState.update { it.copy(isLoading = false, routes = options) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun saveTrip(origin: String, destination: String, option: TransportOption, payerName: String = "") {
        val groupId = _uiState.value.selectedGroupId ?: return
        viewModelScope.launch {
            val trip = TripRecord(
                id = UUID.randomUUID().toString(),
                groupId = groupId,
                origin = origin,
                destination = destination,
                transportMode = option.mode,
                totalCostPln = option.costPln,
                tripDateEpoch = System.currentTimeMillis(),
                payerName = payerName
            )
            tripRepository.saveTrip(trip)
            // Add the trip cost to the group's expenses
            expenseRepository.addExpenseToGroup(groupId, option.costPln, payerName)

            _uiState.update { it.copy(routes = emptyList()) }
        }
    }
}
