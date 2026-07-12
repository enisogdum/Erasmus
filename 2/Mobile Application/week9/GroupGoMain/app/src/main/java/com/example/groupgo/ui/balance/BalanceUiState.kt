package com.example.groupgo.ui.balance

import com.example.groupgo.domain.model.ExpenseBalance
import com.example.groupgo.domain.model.TravelGroup

data class BalanceUiState(
    val groups: List<TravelGroup> = emptyList(),
    val balances: List<ExpenseBalance> = emptyList(),
    val selectedGroupId: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
