package com.borabor.threeinonecompose.ui.screen.timer.countdown

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.borabor.threeinonecompose.R
import com.borabor.threeinonecompose.util.ComposableLifecycle
import com.borabor.threeinonecompose.util.TimerState
import com.borabor.threeinonecompose.util.formatTime

@Composable
fun CountdownScreen() {
    val viewModel: CountdownViewModel = hiltViewModel()

    ComposableLifecycle { event ->
        if (event == Lifecycle.Event.ON_START) viewModel.setInitialCountdown()
        if (event == Lifecycle.Event.ON_STOP) viewModel.saveCountdown()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CountdownText(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 72.dp),
            timeInMillis = viewModel.timeInMillis,
            timerState = viewModel.timerState
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
        ) {
            CountdownSetter(
                state = viewModel.timerState,
                hours = viewModel.hours,
                setHour = { viewModel.setHour(it) },
                minutes = viewModel.minutes,
                setMinute = { viewModel.setMinute(it) },
                seconds = viewModel.seconds,
                setSecond = { viewModel.setSecond(it) }
            )
            CountdownButtons(
                time = viewModel.timeInMillis,
                state = viewModel.timerState,
                start = viewModel::startCountdown,
                pause = viewModel::pauseCountdown,
                stop = viewModel::stopCountdown
            )
        }
    }
}

@Composable
private fun CountdownText(
    modifier: Modifier,
    timeInMillis: Long,
    timerState: TimerState
) {
    val offsetY = 200
    val offsetToTop = IntOffset(x = 0, y = if (timerState != TimerState.STOPPED) -offsetY else 0)
    val offsetToBottom = IntOffset(x = 0, y = offsetY)

    val offsetAnimation = animateIntOffsetAsState(
        targetValue = if (timerState != TimerState.STOPPED) offsetToBottom else offsetToTop,
        animationSpec = tween(durationMillis = 400)
    )

    Text(
        text = timeInMillis.formatTime().dropLast(3),
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier.offset(x = offsetAnimation.value.x.dp, y = offsetAnimation.value.y.dp),
        fontSize = 80.sp,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun CountdownSetter(
    state: TimerState,
    hours: Float,
    setHour: (Float) -> Unit,
    minutes: Float,
    setMinute: (Float) -> Unit,
    seconds: Float,
    setSecond: (Float) -> Unit,
) {
    AnimatedVisibility(visible = state == TimerState.STOPPED) {
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 80.dp)) {
            Text(text = stringResource(id = R.string.countdown_hours))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Slider(
                    value = hours,
                    onValueChange = { setHour(it) },
                    modifier = Modifier.weight(0.9f),
                    valueRange = 0f..23f
                )
                Text(
                    text = hours.toInt().toString(),
                    modifier = Modifier.weight(0.1f),
                    textAlign = TextAlign.End
                )
            }
            Text(text = stringResource(id = R.string.countdown_minutes))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Slider(
                    value = minutes,
                    onValueChange = { setMinute(it) },
                    modifier = Modifier.weight(0.9f),
                    valueRange = 0f..59f
                )
                Text(
                    text = minutes.toInt().toString(),
                    modifier = Modifier.weight(0.1f),
                    textAlign = TextAlign.End
                )
            }
            Text(text = stringResource(id = R.string.countdown_seconds))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Slider(
                    value = seconds,
                    onValueChange = { setSecond(it) },
                    modifier = Modifier.weight(0.9f),
                    valueRange = 0f..59f
                )
                Text(
                    text = seconds.toInt().toString(),
                    modifier = Modifier.weight(0.1f),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
private fun CountdownButtons(
    time: Long,
    state: TimerState,
    start: () -> Unit,
    pause: () -> Unit,
    stop: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        if (state != TimerState.STOPPED) {
            CustomButton(
                drawableId = R.drawable.ic_baseline_stop_24,
                contentDescription = "Stop countdown"
            ) { stop() }
        }

        val context = LocalContext.current
        val toastMessage = stringResource(id = R.string.countdown_timer_not_set)

        CustomButton(
            drawableId = if (state == TimerState.RUNNING) R.drawable.ic_baseline_pause_24 else R.drawable.ic_baseline_play_arrow_24,
            contentDescription = "Play/pause countdown"
        ) {
            when (state) {
                TimerState.RUNNING -> pause()
                else -> if (time > 0L) start() else Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
private fun CustomButton(
    @DrawableRes drawableId: Int,
    contentDescription: String,
    fabSize: Dp = 64.dp,
    fabImageSize: Dp = 32.dp,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = { onClick() },
        modifier = Modifier.size(fabSize),
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 2.dp)
    ) {
        Icon(
            painter = painterResource(id = drawableId),
            contentDescription = contentDescription,
            modifier = Modifier.size(fabImageSize)
        )
    }
}