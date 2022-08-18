package com.borabor.threeinonecompose.util

import androidx.navigation.NavHostController
import java.util.concurrent.TimeUnit

fun NavHostController.navigateWithoutBackstack(route: String) {
    navigate(route = route) {
        popUpTo(graph.startDestinationId) { inclusive = true }
    }
    graph.setStartDestination(route)
}

fun Double.trimResult() = with(String.format("%.10f", this)) {
    if (this.indexOf(".") < 0) this
    else this.replace("0*$".toRegex(), "").replace("\\.$".toRegex(), "")
}

fun Long.formatTime() = String.format(
    "%02d:%02d:%02d.%02d",
    TimeUnit.MILLISECONDS.toHours(this) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(this)),
    TimeUnit.MILLISECONDS.toMinutes(this) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(this)),
    TimeUnit.MILLISECONDS.toSeconds(this) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(this)),
    TimeUnit.MILLISECONDS.toMillis((this / 10) % 100) - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds((this / 10) % 100))
)