package com.borabor.threeinonecompose.ui.screen.timer

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.borabor.threeinonecompose.R
import com.borabor.threeinonecompose.ui.screen.timer.countdown.CountdownScreen
import com.borabor.threeinonecompose.ui.screen.timer.stopwatch.StopwatchScreen
import com.borabor.threeinonecompose.util.ComposableLifecycle
import com.borabor.threeinonecompose.util.Screen
import com.borabor.threeinonecompose.util.navigateWithoutBackstack

@Composable
fun TimerScreen() {
    val viewModel: TimerViewModel = hiltViewModel()
    val timerType = viewModel.timerType
    val navController = rememberNavController()

    ComposableLifecycle { event ->
        if (event == Lifecycle.Event.ON_PAUSE) viewModel.saveTimerType()
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val (navHost, toggle) = createRefs()

        NavHost(
            navController = navController,
            startDestination = when (timerType) {
                TimerViewModel.TimerType.STOPWATCH -> Screen.StopwatchScreen.route
                TimerViewModel.TimerType.COUNTDOWN -> Screen.CountdownScreen.route
            },
            modifier = Modifier
                .constrainAs(navHost) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    bottom.linkTo(toggle.top)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
                .padding(bottom = 64.dp)
        ) {
            composable(route = Screen.StopwatchScreen.route) { StopwatchScreen() }
            composable(route = Screen.CountdownScreen.route) { CountdownScreen() }
        }
        Row(
            modifier = Modifier
                .constrainAs(toggle) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
                .clickable { viewModel.switchTimer() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(
                    id = when (timerType) {
                        TimerViewModel.TimerType.STOPWATCH -> R.drawable.ic_baseline_hourglass_top_24
                        TimerViewModel.TimerType.COUNTDOWN -> R.drawable.ic_baseline_timer_24
                    }
                ),
                contentDescription = "Timer type",
                modifier = Modifier.size(48.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_repeat_24),
                contentDescription = "Switch timer type"
            )
        }

        when (timerType) {
            TimerViewModel.TimerType.STOPWATCH -> navController.navigateWithoutBackstack(Screen.StopwatchScreen.route)
            TimerViewModel.TimerType.COUNTDOWN -> navController.navigateWithoutBackstack(Screen.CountdownScreen.route)
        }
    }
}