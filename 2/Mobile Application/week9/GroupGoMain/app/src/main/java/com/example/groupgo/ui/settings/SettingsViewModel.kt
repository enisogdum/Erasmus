package com.example.groupgo.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groupgo.domain.model.UserSettings
import com.example.groupgo.domain.repository.SettingsRepository
import com.example.groupgo.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.getSettings().collect { settings ->
                _uiState.update { it.copy(settings = settings, isLoading = false) }
            }
        }
    }

    fun updateSalary(salary: Double) {
        viewModelScope.launch {
            val current = _uiState.value.settings
            settingsRepository.saveSettings(current.copy(monthlySalaryPln = salary))
        }
    }

    fun updateHours(hours: Double) {
        viewModelScope.launch {
            val current = _uiState.value.settings
            settingsRepository.saveSettings(current.copy(avgWorkingHoursPerMonth = hours))
        }
    }

    fun getSessionUser(): String? = authRepository.getSessionUser()

    fun getUserTier(email: String): String = authRepository.getUserTier(email)

    fun logout() {
        authRepository.logout()
    }
}
