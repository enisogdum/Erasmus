package com.example.groupgo.ui.home

import com.example.groupgo.domain.model.TransportOption
import com.example.groupgo.domain.model.TravelGroup

data class HomeUiState(
    val groups: List<TravelGroup> = emptyList(),
    val selectedGroupId: String? = null,
    val routes: List<TransportOption> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedPriority: String? = null
)
