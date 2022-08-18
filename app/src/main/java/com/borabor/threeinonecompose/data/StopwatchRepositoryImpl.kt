package com.borabor.threeinonecompose.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.borabor.threeinonecompose.util.PreferencesKeys
import com.borabor.threeinonecompose.util.TimerState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StopwatchRepositoryImpl @Inject constructor(private val dataStore: DataStore<Preferences>) : StopwatchRepository {
    override fun saveStopwatch(timeInMillis: Long, timerState: TimerState, lapList: List<Pair<Long, Long>>) {
        runBlocking {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.STOPWATCH_TIME] = timeInMillis
            }

            dataStore.edit { preferences ->
                preferences[PreferencesKeys.STOPWATCH_STATE] = timerState.ordinal
            }

            dataStore.edit { preferences ->
                preferences[PreferencesKeys.STOPWATCH_LAP_LIST] = Gson().toJson(lapList)
            }

            dataStore.edit { preferences ->
                preferences[PreferencesKeys.STOPWATCH_LAST_SYSTEM_TIME] = System.currentTimeMillis()
            }
        }
    }

    override suspend fun getStopwatchTime(): Long = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.STOPWATCH_TIME] ?: 0L
    }.first()

    override suspend fun getStopwatchState(): TimerState = dataStore.data.map { preferences ->
        val ordinal = preferences[PreferencesKeys.STOPWATCH_STATE] ?: TimerState.STOPPED.ordinal
        TimerState.values()[ordinal]
    }.first()

    override suspend fun getStopwatchLapList(): List<Pair<Long, Long>> = dataStore.data.map { preferences ->
        val defValue = Gson().toJson(listOf<Pair<Long, Long>>())
        val json = preferences[PreferencesKeys.STOPWATCH_LAP_LIST] ?: defValue
        val token = object : TypeToken<List<Pair<Long, Long>>>() {}
        Gson().fromJson<List<Pair<Long, Long>>>(json, token.type)
    }.first()

    override suspend fun getStopwatchLastSystemTime(): Long = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.STOPWATCH_LAST_SYSTEM_TIME] ?: 0L
    }.first()
}