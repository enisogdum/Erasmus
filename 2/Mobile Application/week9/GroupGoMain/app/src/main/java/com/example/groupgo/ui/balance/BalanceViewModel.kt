package com.example.groupgo.ui.balance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groupgo.domain.repository.ExpenseRepository
import com.example.groupgo.domain.repository.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BalanceViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val groupRepository: GroupRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BalanceUiState())
    val uiState: StateFlow<BalanceUiState> = _uiState.asStateFlow()

    private var collectionJob: Job? = null

    init {
        viewModelScope.launch {
            groupRepository.getAllGroups().collect { groups ->
                _uiState.update { it.copy(groups = groups) }
            }
        }
    }

    fun selectGroup(groupId: String) {
        _uiState.update { it.copy(selectedGroupId = groupId, isLoading = true) }
        
        collectionJob?.cancel()
        collectionJob = viewModelScope.launch {
            expenseRepository.getBalancesForGroup(groupId).collect { balances ->
                _uiState.update { it.copy(balances = balances, isLoading = false) }
            }
        }
    }

    fun updateMemberPaid(memberName: String, amount: Double) {
        val groupId = _uiState.value.selectedGroupId ?: return
        viewModelScope.launch {
            expenseRepository.updateAmountPaid(groupId, memberName, amount)
        }
    }
}
