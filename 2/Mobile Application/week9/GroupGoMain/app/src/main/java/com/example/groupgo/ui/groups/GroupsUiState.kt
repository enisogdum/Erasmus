package com.example.groupgo.ui.groups

import com.example.groupgo.domain.model.TravelGroup
import com.example.groupgo.domain.model.TripRecord
import com.example.groupgo.domain.model.ExpenseBalance

data class GroupsUiState(
    val groups: List<TravelGroup> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedGroup: TravelGroup? = null,
    val selectedGroupTrips: List<TripRecord> = emptyList(),
    val selectedGroupBalances: List<ExpenseBalance> = emptyList()
)
