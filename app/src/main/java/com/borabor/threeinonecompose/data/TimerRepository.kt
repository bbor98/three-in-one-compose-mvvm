package com.borabor.threeinonecompose.data

import com.borabor.threeinonecompose.ui.screen.timer.TimerViewModel

interface TimerRepository {
    fun saveTimerType(timerType: TimerViewModel.TimerType)
    suspend fun getTimerType(): TimerViewModel.TimerType
}