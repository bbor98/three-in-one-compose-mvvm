package com.borabor.threeinonecompose.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.borabor.threeinonecompose.ui.screen.timer.TimerViewModel
import com.borabor.threeinonecompose.util.PreferencesKeys
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimerRepositoryImpl @Inject constructor(private val dataStore: DataStore<Preferences>) : TimerRepository {
    override fun saveTimerType(timerType: TimerViewModel.TimerType) {
        runBlocking {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.TIMER_TYPE] = timerType.ordinal
            }
        }
    }

    override suspend fun getTimerType(): TimerViewModel.TimerType = dataStore.data.map { preferences ->
        val ordinal = preferences[PreferencesKeys.TIMER_TYPE] ?: TimerViewModel.TimerType.STOPWATCH.ordinal
        TimerViewModel.TimerType.values()[ordinal]
    }.first()
}