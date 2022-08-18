package com.borabor.threeinonecompose.ui.screen.timer.stopwatch

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.borabor.threeinonecompose.R
import com.borabor.threeinonecompose.util.ComposableLifecycle
import com.borabor.threeinonecompose.util.TimerState
import com.borabor.threeinonecompose.util.formatTime

@Composable
fun StopwatchScreen() {
    val viewModel: StopwatchViewModel = hiltViewModel()

    ComposableLifecycle { event ->
        if (event == Lifecycle.Event.ON_START) viewModel.setInitialStopwatch()
        if (event == Lifecycle.Event.ON_STOP) viewModel.saveStopwatch()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        StopwatchText(
            time = viewModel.timeInMillis,
            isLapped = viewModel.lapList.isNotEmpty()
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
        ) {
            LapList(
                lapList = viewModel.lapList,
                clearList = viewModel::clearLapList
            )
            StopwatchButtons(
                state = viewModel.timerState,
                start = viewModel::startStopwatch,
                pause = viewModel::pauseStopwatch,
                stop = viewModel::stopStopwatch,
                lap = viewModel::lap
            )
        }
    }
}

private const val animDuration = 400

@Composable
private fun StopwatchText(
    time: Long,
    isLapped: Boolean
) {
    val offsetY = 230
    val offsetToTop = IntOffset(x = 0, y = -offsetY)
    val offsetToBottom = IntOffset(x = 0, y = if (isLapped) offsetY else 0)

    val offsetAnimation = animateIntOffsetAsState(
        targetValue = if (isLapped) offsetToTop else offsetToBottom,
        animationSpec = tween(durationMillis = animDuration)
    )

    Column(modifier = Modifier.offset(x = offsetAnimation.value.x.dp, y = offsetAnimation.value.y.dp)) {
        val stopwatchText = time.formatTime()
        val dayCounter = time / (60 * 60 * 24 * 1000)

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = stopwatchText.dropLast(3),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 70.sp
            )
            Text(
                text = stopwatchText.takeLast(3),
                modifier = Modifier.offset(x = 0.dp, y = (-10).dp),
                color = Color.Gray,
                fontSize = 35.sp
            )
        }
        if (dayCounter > 0) {
            Text(
                text = "+$dayCounter ${stringResource(id = R.string.stopwatch_day)}",
                color = Color.Gray,
                fontSize = 20.sp
            )
        }
    }
}

@Composable
private fun LapList(
    lapList: List<Pair<Long, Long>>,
    clearList: () -> Unit
) {
    val isLapped = lapList.isNotEmpty()
    val listState: LazyListState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isLapped) (LocalConfiguration.current.screenHeightDp / 2).dp else 0.dp)
            .padding(bottom = 16.dp)
            .animateContentSize(animationSpec = tween(durationMillis = animDuration))
    ) {
        CustomButton(
            drawableId = R.drawable.ic_baseline_delete_24,
            contentDescription = "Clear lap list",
            modifier = Modifier
                .align(Alignment.End)
                .padding(bottom = 8.dp),
            visible = isLapped,
            fabSize = 48.dp,
            fabImageSize = 24.dp
        ) { clearList() }
        LapListItem( // same layout as header
            lapNumber = stringResource(id = R.string.stopwatch_lap_number),
            lapTime = stringResource(id = R.string.stopwatch_lap_time),
            lapInterval = stringResource(id = R.string.stopwatch_lap_interval),
            fontWeight = FontWeight.Bold
        )
        Divider(
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
            color = MaterialTheme.colorScheme.onBackground
        )
        LazyColumn(state = listState) {
            itemsIndexed(items = lapList) { index, item ->
                val dayInMilliseconds = 60 * 60 * 24 * 1000
                val dayCounterLapTime = item.first / dayInMilliseconds
                val dayTextLapTime = if (dayCounterLapTime > 0) "(+$dayCounterLapTime ${stringResource(id = R.string.stopwatch_day)}) " else ""

                val dayCounterLapInterval = item.second / dayInMilliseconds
                val dayTextLapInterval = if (dayCounterLapInterval > 0) "(+$dayCounterLapInterval ${stringResource(id = R.string.stopwatch_day)}) " else ""

                LapListItem(
                    lapNumber = (index + 1).toString(),
                    lapTime = dayTextLapTime + item.first.formatTime(),
                    lapInterval = "$dayTextLapInterval+${item.second.formatTime()}"
                )
            }
        }

        LaunchedEffect(key1 = lapList.size) {
            if (isLapped) listState.animateScrollToItem(lapList.lastIndex)
        }
    }
}

@Composable
private fun LapListItem(
    lapNumber: String,
    lapTime: String,
    lapInterval: String,
    color: Color = MaterialTheme.colorScheme.onBackground,
    fontSize: TextUnit = 20.sp,
    fontWeight: FontWeight = FontWeight.Normal
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 4.dp)
    ) {
        Text(
            text = lapNumber,
            modifier = Modifier.weight(0.2f),
            color = color,
            fontSize = fontSize,
            fontWeight = fontWeight
        )
        Text(
            text = lapTime,
            modifier = Modifier.weight(0.4f),
            color = color,
            fontSize = fontSize,
            fontWeight = fontWeight
        )
        Text(
            text = lapInterval,
            modifier = Modifier.weight(0.4f),
            color = color,
            fontSize = fontSize,
            fontWeight = fontWeight
        )
    }
}

@Composable
private fun StopwatchButtons(
    state: TimerState,
    start: () -> Unit,
    pause: () -> Unit,
    stop: () -> Unit,
    lap: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        CustomButton(
            drawableId = R.drawable.ic_baseline_stop_24,
            contentDescription = "Stop stopwatch",
            visible = state != TimerState.STOPPED
        ) { stop() }
        CustomButton(
            drawableId = if (state == TimerState.RUNNING) R.drawable.ic_baseline_pause_24 else R.drawable.ic_baseline_play_arrow_24,
            contentDescription = "Play/pause stopwatch"
        ) { if (state == TimerState.RUNNING) pause() else start() }
        CustomButton(
            drawableId = R.drawable.ic_baseline_tour_24,
            contentDescription = "Lap",
            visible = state != TimerState.STOPPED
        ) { lap() }
    }
}

@Composable
private fun CustomButton(
    @DrawableRes drawableId: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    fabSize: Dp = 64.dp,
    fabImageSize: Dp = 32.dp,
    onClick: () -> Unit
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = fadeIn(animationSpec = tween(durationMillis = animDuration)),
        exit = fadeOut(animationSpec = tween(durationMillis = animDuration))
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
}