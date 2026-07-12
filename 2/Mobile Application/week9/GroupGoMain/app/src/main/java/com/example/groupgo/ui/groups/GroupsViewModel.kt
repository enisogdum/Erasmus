package com.example.groupgo.ui.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groupgo.domain.model.TravelGroup
import com.example.groupgo.domain.repository.ExpenseRepository
import com.example.groupgo.domain.repository.GroupRepository
import com.example.groupgo.domain.repository.TripRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val expenseRepository: ExpenseRepository,
    private val tripRepository: TripRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroupsUiState())
    val uiState: StateFlow<GroupsUiState> = _uiState.asStateFlow()

    private var tripsJob: Job? = null
    private var balancesJob: Job? = null

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            groupRepository.getAllGroups().collect { groups ->
                _uiState.update { it.copy(groups = groups, isLoading = false) }
            }
        }
    }

    fun selectGroup(group: TravelGroup?) {
        tripsJob?.cancel()
        balancesJob?.cancel()

        if (group == null) {
            _uiState.update {
                it.copy(
                    selectedGroup = null,
                    selectedGroupTrips = emptyList(),
                    selectedGroupBalances = emptyList()
                )
            }
            return
        }

        _uiState.update { it.copy(selectedGroup = group) }

        tripsJob = viewModelScope.launch {
            tripRepository.getTripsByGroup(group.id).collect { trips ->
                _uiState.update { it.copy(selectedGroupTrips = trips) }
            }
        }

        balancesJob = viewModelScope.launch {
            expenseRepository.getBalancesForGroup(group.id).collect { balances ->
                _uiState.update { it.copy(selectedGroupBalances = balances) }
            }
        }
    }

    fun addGroup(name: String, members: List<String>) {
        viewModelScope.launch {
            val groupId = UUID.randomUUID().toString()
            val newGroup = TravelGroup(
                id = groupId,
                name = name,
                memberNames = members,
                createdAtEpoch = System.currentTimeMillis()
            )
            groupRepository.createGroup(newGroup)
            expenseRepository.initializeGroupBalances(groupId, members)
        }
    }

    fun deleteGroup(groupId: String) {
        viewModelScope.launch {
            groupRepository.deleteGroup(groupId)
            expenseRepository.clearGroupBalances(groupId)
        }
    }
}
