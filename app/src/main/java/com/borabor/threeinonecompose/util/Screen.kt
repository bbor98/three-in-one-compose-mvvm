package com.borabor.threeinonecompose.util

sealed class Screen(val route: String) {
    object HomeScreen : Screen(route = "home_screen")
    object CalculatorScreen : Screen(route = "calculator_screen")
    object UnitConverterScreen : Screen(route = "unit_converter_screen")
    object TimerScreen : Screen(route = "timer_screen")
    object StopwatchScreen : Screen(route = "stopwatch_screen")
    object CountdownScreen : Screen(route = "countdown_screen")
}