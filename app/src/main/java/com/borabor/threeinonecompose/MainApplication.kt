package com.borabor.threeinonecompose

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.borabor.threeinonecompose.util.PreferencesKeys
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application() {

    @Inject
    lateinit var dataStore: DataStore<Preferences>

    var isDark by mutableStateOf(false)
        private set

    override fun onCreate() {
        super.onCreate()
        isDark = getIsDarkTheme(dataStore)
    }

    fun toggleDarkTheme() {
        isDark = !isDark
        setIsDarkTheme(dataStore, isDark)
    }

    private fun getIsDarkTheme(dataStore: DataStore<Preferences>): Boolean {
        val preferences = runBlocking { dataStore.data.first() }
        return preferences[PreferencesKeys.IS_DARK_THEME] ?: false
    }

    private fun setIsDarkTheme(dataStore: DataStore<Preferences>, isDarkTheme: Boolean) {
        runBlocking {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.IS_DARK_THEME] = isDarkTheme
            }
        }
    }
}