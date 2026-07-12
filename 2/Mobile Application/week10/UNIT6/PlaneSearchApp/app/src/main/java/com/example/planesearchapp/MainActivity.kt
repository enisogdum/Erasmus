package com.example.planesearchapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.planesearchapp.data.FlightDatabase
import com.example.planesearchapp.data.FlightRepository
import com.example.planesearchapp.data.UserPreferencesRepository
import com.example.planesearchapp.ui.FlightSearchApp
import com.example.planesearchapp.ui.FlightSearchViewModel
import com.example.planesearchapp.ui.FlightSearchViewModelFactory
import com.example.planesearchapp.ui.theme.PlaneSearchAppTheme

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "search_preferences")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = FlightDatabase.getDatabase(this)
        val flightRepository = FlightRepository(database.airportDao())
        val userPreferencesRepository = UserPreferencesRepository(dataStore)

        val viewModel: FlightSearchViewModel by viewModels {
            FlightSearchViewModelFactory(flightRepository, userPreferencesRepository)
        }

        setContent {
            PlaneSearchAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FlightSearchApp(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}