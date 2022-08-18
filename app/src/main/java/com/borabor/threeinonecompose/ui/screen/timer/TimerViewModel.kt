package com.borabor.threeinonecompose.ui.screen.timer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.borabor.threeinonecompose.data.TimerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerViewModel @Inject constructor(private val repository: TimerRepository) : ViewModel() {

    enum class TimerType {
        STOPWATCH, COUNTDOWN
    }

    var timerType by mutableStateOf(TimerType.STOPWATCH)
        private set

    init {
        viewModelScope.launch {
            timerType = repository.getTimerType()
        }
    }

    fun switchTimer() {
        timerType = when (timerType) {
            TimerType.STOPWATCH -> TimerType.COUNTDOWN
            TimerType.COUNTDOWN -> TimerType.STOPWATCH
        }
    }

    fun saveTimerType() {
        repository.saveTimerType(timerType)
    }
}