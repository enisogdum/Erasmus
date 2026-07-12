package com.example.groupgo.ui.history

import com.example.groupgo.domain.model.TripRecord

data class HistoryUiState(
    val trips: List<TripRecord> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
