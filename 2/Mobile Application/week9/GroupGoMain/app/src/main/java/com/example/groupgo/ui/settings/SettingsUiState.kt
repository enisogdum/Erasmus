package com.example.groupgo.ui.settings

import com.example.groupgo.domain.model.UserSettings

data class SettingsUiState(
    val settings: UserSettings = UserSettings(
        monthlySalaryPln = 0.0,
        avgWorkingHoursPerMonth = 160.0
    ),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
