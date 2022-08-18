package com.borabor.threeinonecompose.ui.screen.timer.stopwatch

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.borabor.threeinonecompose.data.StopwatchRepository
import com.borabor.threeinonecompose.util.TimerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StopwatchViewModel @Inject constructor(private val repository: StopwatchRepository) : ViewModel() {

    var timeInMillis by mutableStateOf(0L)
        private set

    var timerState by mutableStateOf(TimerState.STOPPED)
        private set

    private val _lapList = mutableStateListOf<Pair<Long, Long>>()
    val lapList: List<Pair<Long, Long>> = _lapList

    private var lastSystemTime = 0L

    private var timeStamp = 0L

    private val handler by lazy { Handler(Looper.getMainLooper()) }

    private val runnable by lazy {
        object : Runnable {
            override fun run() {
                timeInMillis += System.currentTimeMillis() - timeStamp
                timeStamp = System.currentTimeMillis()
                handler.postDelayed(this, 10L)
            }
        }
    }

    fun startStopwatch() {
        timeStamp = System.currentTimeMillis()
        handler.post(runnable)
        timerState = TimerState.RUNNING
    }

    fun pauseStopwatch() {
        handler.removeCallbacks(runnable)
        timerState = TimerState.PAUSED
    }

    fun stopStopwatch() {
        handler.removeCallbacks(runnable)
        clearLapList()
        timeInMillis = 0L
        timerState = TimerState.STOPPED
    }

    fun lap() {
        val lapInterval = timeInMillis - (lapList.lastOrNull()?.first ?: 0L)
        _lapList.add(Pair(timeInMillis, lapInterval))
    }

    fun clearLapList() {
        _lapList.clear()
    }

    fun setInitialStopwatch() {
        viewModelScope.launch {
            repository.apply {
                timeInMillis = getStopwatchTime()
                timerState = getStopwatchState()
                if (!_lapList.containsAll(getStopwatchLapList())) _lapList.addAll(getStopwatchLapList())
                lastSystemTime = getStopwatchLastSystemTime()
            }
        }

        if (timerState == TimerState.RUNNING) {
            timeInMillis += System.currentTimeMillis() - lastSystemTime
            startStopwatch()
        }
    }

    fun saveStopwatch() {
        handler.removeCallbacks(runnable)

        repository.saveStopwatch(
            timeInMillis = timeInMillis,
            timerState = timerState,
            lapList = lapList
        )
    }
}