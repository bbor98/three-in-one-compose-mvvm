package com.borabor.threeinonecompose.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.borabor.threeinonecompose.R
import com.borabor.threeinonecompose.util.Screen

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomImage(
            navController = navController,
            route = Screen.CalculatorScreen.route,
            drawableId = R.drawable.ic_baseline_calculate_24,
            contentDescription = "Calculator"
        )
        CustomImage(
            navController = navController,
            route = Screen.UnitConverterScreen.route,
            drawableId = R.drawable.ic_baseline_repeat_24,
            contentDescription = "Unit Converter"
        )
        CustomImage(
            navController = navController,
            route = Screen.TimerScreen.route,
            drawableId = R.drawable.ic_baseline_timer_24,
            contentDescription = "Timer"
        )
    }
}

@Composable
private fun CustomImage(
    navController: NavController,
    route: String,
    drawableId: Int,
    contentDescription: String
) {
    Image(
        painter = painterResource(id = drawableId),
        contentDescription = contentDescription,
        modifier = Modifier
            .size(100.dp)
            .clickable { navController.navigate(route = route) },
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
    )
}