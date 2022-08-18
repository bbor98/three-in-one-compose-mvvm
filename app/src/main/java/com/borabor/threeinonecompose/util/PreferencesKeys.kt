package com.borabor.threeinonecompose.util

import androidx.datastore.preferences.core.*

object PreferencesKeys {
    val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")

    val CALCULATOR_OPERATIONS = stringPreferencesKey("calculator_operations")
    val CALCULATOR_RESULT = stringPreferencesKey("calculator_result")
    val CALCULATOR_IS_EXPANDED = booleanPreferencesKey("calculator_is_expanded")
    val CALCULATOR_IS_SECONDARY = booleanPreferencesKey("calculator_is_secondary")
    val CALCULATOR_ANGLE_TYPE = intPreferencesKey("calculator_angle_type")

    val UNIT_CONVERTER_INPUT = stringPreferencesKey("unit_converter_input")
    val UNIT_CONVERTER_OUTPUT = stringPreferencesKey("unit_converter_output")
    val UNIT_CONVERTER_SELECTED_TAB_INDEX = intPreferencesKey("unit_converter_selected_tab_index")
    val UNIT_CONVERTER_SUBUNIT_1 = intPreferencesKey("unit_converter_subunit_1")
    val UNIT_CONVERTER_SUBUNIT_2 = intPreferencesKey("unit_converter_subunit_2")
    val UNIT_CONVERTER_REVERSED = booleanPreferencesKey("unit_converter_reversed")

    val TIMER_TYPE = intPreferencesKey("timer_type")

    val STOPWATCH_TIME = longPreferencesKey("stopwatch_time")
    val STOPWATCH_STATE = intPreferencesKey("stopwatch_state")
    val STOPWATCH_LAP_LIST = stringPreferencesKey("stopwatch_lap_list")
    val STOPWATCH_LAST_SYSTEM_TIME = longPreferencesKey("stopwatch_last_system_time")

    val COUNTDOWN_TIME = longPreferencesKey("countdown_time")
    val COUNTDOWN_STATE = intPreferencesKey("countdown_state")
    val COUNTDOWN_HOURS = floatPreferencesKey("countdown_hours")
    val COUNTDOWN_MINUTES = floatPreferencesKey("countdown_minutes")
    val COUNTDOWN_SECONDS = floatPreferencesKey("countdown_seconds")
    val COUNTDOWN_LAST_SYSTEM_TIME = longPreferencesKey("countdown_last_system_time")
}