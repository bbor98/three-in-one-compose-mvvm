package com.borabor.threeinonecompose.ui.screen.timer.countdown

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.borabor.threeinonecompose.data.CountdownRepository
import com.borabor.threeinonecompose.util.TimerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CountdownViewModel @Inject constructor(private val repository: CountdownRepository) : ViewModel() {

    var timeInMillis by mutableStateOf(0L)
        private set

    var timerState by mutableStateOf(TimerState.STOPPED)
        private set

    var hours by mutableStateOf(0f)
        private set

    var minutes by mutableStateOf(0f)
        private set

    var seconds by mutableStateOf(0f)
        private set

    private var lastSystemTime = 0L

    private var timeStamp = System.currentTimeMillis()

    private val handler by lazy { Handler(Looper.getMainLooper()) }

    private val runnable by lazy {
        object : Runnable {
            override fun run() {
                if (timeInMillis <= 0L) {
                    stopCountdown()
                    return
                }

                timeInMillis -= System.currentTimeMillis() - timeStamp
                timeStamp = System.currentTimeMillis()
                handler.postDelayed(this, 10L)
            }
        }
    }

    fun startCountdown() {
        timeStamp = System.currentTimeMillis()
        handler.post(runnable)
        timerState = TimerState.RUNNING
    }

    fun pauseCountdown() {
        handler.removeCallbacks(runnable)
        timerState = TimerState.PAUSED
    }

    fun stopCountdown() {
        handler.removeCallbacks(runnable)
        timeInMillis = 0L
        timerState = TimerState.STOPPED
        hours = 0f
        minutes = 0f
        seconds = 0f
    }

    fun setHour(value: Float) {
        hours = value
        setCountdownTime()
    }

    fun setMinute(value: Float) {
        minutes = value
        setCountdownTime()
    }

    fun setSecond(value: Float) {
        seconds = value
        setCountdownTime()
    }

    private fun setCountdownTime() {
        timeInMillis = hours.toLong() * 60 * 60 * 1000 + minutes.toLong() * 60 * 1000 + seconds.toLong() * 1000
    }

    fun setInitialCountdown() {
        viewModelScope.launch {
            repository.apply {
                timeInMillis = getCountdownTime()
                timerState = getCountdownState()
                hours = getCountdownHours()
                minutes = getCountdownMinutes()
                seconds = getCountdownSeconds()
                lastSystemTime = getCountdownLastSystemTime()
            }
        }

        if (timerState == TimerState.RUNNING) {
            timeInMillis -= System.currentTimeMillis() - lastSystemTime
            startCountdown()
        }
    }

    fun saveCountdown() {
        handler.removeCallbacks(runnable)

        repository.saveCountdown(
            timeInMillis = timeInMillis,
            timerState = timerState,
            hours = hours,
            minutes = minutes,
            seconds = seconds
        )
    }
}