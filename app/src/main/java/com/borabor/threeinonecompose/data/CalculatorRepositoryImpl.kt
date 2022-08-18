package com.borabor.threeinonecompose.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.borabor.threeinonecompose.ui.screen.calculator.CalculatorViewModel
import com.borabor.threeinonecompose.util.PreferencesKeys
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalculatorRepositoryImpl @Inject constructor(private val dataStore: DataStore<Preferences>) : CalculatorRepository {
    override fun saveCalculator(operationsList: List<Pair<String, String>>, resultText: String, isExpanded: Boolean, isSecondary: Boolean, angleType: CalculatorViewModel.AngleType) {
        runBlocking {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.CALCULATOR_RESULT] = resultText
                preferences[PreferencesKeys.CALCULATOR_OPERATIONS] = Gson().toJson(operationsList)
                preferences[PreferencesKeys.CALCULATOR_IS_EXPANDED] = isExpanded
                preferences[PreferencesKeys.CALCULATOR_IS_SECONDARY] = isSecondary
                preferences[PreferencesKeys.CALCULATOR_ANGLE_TYPE] = angleType.ordinal
            }
        }
    }

    override suspend fun getOperationsList(): List<Pair<String, String>> = dataStore.data.map { preferences ->
        val defValue = Gson().toJson(listOf<Pair<String, String>>())
        val json = preferences[PreferencesKeys.CALCULATOR_OPERATIONS] ?: defValue
        val token = object : TypeToken<List<Pair<String, String>>>() {}
        Gson().fromJson<List<Pair<String, String>>>(json, token.type)
    }.first()

    override suspend fun getResultText(): String = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.CALCULATOR_RESULT] ?: ""
    }.first()

    override suspend fun getIsExpanded(): Boolean = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.CALCULATOR_IS_EXPANDED] ?: false
    }.first()

    override suspend fun getIsSecondary(): Boolean = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.CALCULATOR_IS_SECONDARY] ?: false
    }.first()

    override suspend fun getAngleType(): CalculatorViewModel.AngleType = dataStore.data.map { preferences ->
        val ordinal = preferences[PreferencesKeys.CALCULATOR_ANGLE_TYPE] ?: CalculatorViewModel.AngleType.DEGREE.ordinal
        CalculatorViewModel.AngleType.values()[ordinal]
    }.first()
}