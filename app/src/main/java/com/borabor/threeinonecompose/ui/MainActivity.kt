package com.borabor.threeinonecompose.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.borabor.threeinonecompose.MainApplication
import com.borabor.threeinonecompose.R
import com.borabor.threeinonecompose.ui.screen.HomeScreen
import com.borabor.threeinonecompose.ui.screen.calculator.CalculatorScreen
import com.borabor.threeinonecompose.ui.screen.timer.TimerScreen
import com.borabor.threeinonecompose.ui.screen.unitconverter.UnitConverterScreen
import com.borabor.threeinonecompose.ui.theme.ThreeInOneComposeTheme
import com.borabor.threeinonecompose.util.Screen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var application: MainApplication

    private val isHome = MutableStateFlow(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ThreeInOneComposeTheme(darkTheme = application.isDark) {
                // A surface container using the 'background' color from the theme
                val context = LocalContext.current
                val navController = rememberNavController()
                var actionBarTitle by remember { mutableStateOf("") }

                LaunchedEffect(navController) {
                    navController.currentBackStackEntryFlow.collect { backStackEntry ->
                        actionBarTitle = getTitleByRoute(context, backStackEntry.destination.route.toString())
                    }
                }

                Scaffold(
                    topBar = {
                        SmallTopAppBar(
                            title = { Text(actionBarTitle) },
                            navigationIcon = {
                                if (!isHome.collectAsState().value) {
                                    IconButton(onClick = { navController.popBackStack() }) {
                                        Icon(
                                            imageVector = Icons.Filled.ArrowBack,
                                            contentDescription = "Back arrow",
                                            tint = MaterialTheme.colorScheme.background
                                        )
                                    }
                                }
                            },
                            actions = {
                                IconButton(onClick = { application.toggleDarkTheme() }) {
                                    Icon(
                                        painter = painterResource(id = if (application.isDark) R.drawable.ic_baseline_dark_mode_24 else R.drawable.ic_outline_dark_mode_24),
                                        contentDescription = "Toggle dark theme",
                                        tint = MaterialTheme.colorScheme.background
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.smallTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = MaterialTheme.colorScheme.background
                            )
                        )
                    }
                ) {
                    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                        NavHost(
                            navController = navController,
                            startDestination = Screen.HomeScreen.route
                        ) {
                            composable(route = Screen.HomeScreen.route) { HomeScreen(navController) }
                            composable(route = Screen.CalculatorScreen.route) { CalculatorScreen() }
                            composable(route = Screen.UnitConverterScreen.route) { UnitConverterScreen() }
                            composable(route = Screen.TimerScreen.route) { TimerScreen() }
                        }
                    }
                }
            }
        }
    }

    private fun getTitleByRoute(context: Context, route: String): String {
        isHome.value = false

        return when (route) {
            Screen.CalculatorScreen.route -> context.getString(R.string.calculator)
            Screen.UnitConverterScreen.route -> context.getString(R.string.unit_converter)
            Screen.TimerScreen.route -> context.getString(R.string.timer)
            else -> context.getString(R.string.app_name).also { isHome.value = true }
        }
    }
}