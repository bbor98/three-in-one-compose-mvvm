package com.borabor.threeinonecompose.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.borabor.threeinonecompose.util.PreferencesKeys
import com.borabor.threeinonecompose.util.TimerState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CountdownRepositoryImpl @Inject constructor(private val dataStore: DataStore<Preferences>) : CountdownRepository {
    override fun saveCountdown(timeInMillis: Long, timerState: TimerState, hours: Float, minutes: Float, seconds: Float) {
        runBlocking {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.COUNTDOWN_TIME] = timeInMillis
            }

            dataStore.edit { preferences ->
                preferences[PreferencesKeys.COUNTDOWN_STATE] = timerState.ordinal
            }

            dataStore.edit { preferences ->
                preferences[PreferencesKeys.COUNTDOWN_HOURS] = hours
            }

            dataStore.edit { preferences ->
                preferences[PreferencesKeys.COUNTDOWN_MINUTES] = minutes
            }

            dataStore.edit { preferences ->
                preferences[PreferencesKeys.COUNTDOWN_SECONDS] = seconds
            }

            dataStore.edit { preferences ->
                preferences[PreferencesKeys.COUNTDOWN_LAST_SYSTEM_TIME] = System.currentTimeMillis()
            }
        }
    }

    override suspend fun getCountdownTime(): Long = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.COUNTDOWN_TIME] ?: 0L
    }.first()

    override suspend fun getCountdownState(): TimerState = dataStore.data.map { preferences ->
        val ordinal = preferences[PreferencesKeys.COUNTDOWN_STATE] ?: TimerState.STOPPED.ordinal
        TimerState.values()[ordinal]
    }.first()

    override suspend fun getCountdownHours(): Float = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.COUNTDOWN_HOURS] ?: 0f
    }.first()

    override suspend fun getCountdownMinutes(): Float = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.COUNTDOWN_MINUTES] ?: 0f
    }.first()

    override suspend fun getCountdownSeconds(): Float = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.COUNTDOWN_SECONDS] ?: 0f
    }.first()

    override suspend fun getCountdownLastSystemTime(): Long = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.COUNTDOWN_LAST_SYSTEM_TIME] ?: 0L
    }.first()
}