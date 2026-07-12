package com.example.groupgo.domain.repository

import com.example.groupgo.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<UserSettings> // from DataStore
    suspend fun saveSettings(settings: UserSettings)
}
