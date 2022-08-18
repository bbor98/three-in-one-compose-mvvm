package com.borabor.threeinonecompose.data

import com.borabor.threeinonecompose.util.TimerState

interface CountdownRepository {
    fun saveCountdown(timeInMillis: Long, timerState: TimerState, hours: Float, minutes: Float, seconds: Float)
    suspend fun getCountdownTime(): Long
    suspend fun getCountdownState(): TimerState
    suspend fun getCountdownHours(): Float
    suspend fun getCountdownMinutes(): Float
    suspend fun getCountdownSeconds(): Float
    suspend fun getCountdownLastSystemTime(): Long
}