package com.example.groupgo.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.groupgo.domain.model.UserSettings
import com.example.groupgo.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    private val salaryKey = doublePreferencesKey("monthly_salary_pln")
    private val hoursKey = doublePreferencesKey("avg_working_hours_per_month")

    override fun getSettings(): Flow<UserSettings> =
        context.dataStore.data.map { prefs ->
            UserSettings(
                monthlySalaryPln = prefs[salaryKey] ?: 0.0,
                avgWorkingHoursPerMonth = prefs[hoursKey] ?: 160.0
            )
        }

    override suspend fun saveSettings(settings: UserSettings) {
        context.dataStore.edit { prefs ->
            prefs[salaryKey] = settings.monthlySalaryPln
            prefs[hoursKey] = settings.avgWorkingHoursPerMonth
        }
    }
}
