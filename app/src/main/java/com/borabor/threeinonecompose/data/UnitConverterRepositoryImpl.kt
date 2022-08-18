package com.borabor.threeinonecompose.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.borabor.threeinonecompose.util.PreferencesKeys
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnitConverterRepositoryImpl @Inject constructor(private val dataStore: DataStore<Preferences>) : UnitConverterRepository {
    override fun saveUnitConverter(inputText: String, outputText: String, selectedUnitIndex: Int, subunitIndex1: Int, subunitIndex2: Int, isReversed: Boolean) {
        runBlocking {
            dataStore.edit { preferences ->
                preferences[PreferencesKeys.UNIT_CONVERTER_INPUT] = inputText
                preferences[PreferencesKeys.UNIT_CONVERTER_OUTPUT] = outputText
                preferences[PreferencesKeys.UNIT_CONVERTER_SELECTED_TAB_INDEX] = selectedUnitIndex
                preferences[PreferencesKeys.UNIT_CONVERTER_SUBUNIT_1] = subunitIndex1
                preferences[PreferencesKeys.UNIT_CONVERTER_SUBUNIT_2] = subunitIndex2
                preferences[PreferencesKeys.UNIT_CONVERTER_REVERSED] = isReversed
            }
        }
    }

    override suspend fun getInputText(): String = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.UNIT_CONVERTER_INPUT] ?: ""
    }.first()

    override suspend fun getOutputText(): String = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.UNIT_CONVERTER_OUTPUT] ?: ""
    }.first()

    override suspend fun getSelectedUnitIndex(): Int = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.UNIT_CONVERTER_SELECTED_TAB_INDEX] ?: 0
    }.first()

    override suspend fun getSubunitIndex1(): Int = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.UNIT_CONVERTER_SUBUNIT_1] ?: 0
    }.first()

    override suspend fun getSubunitIndex2(): Int = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.UNIT_CONVERTER_SUBUNIT_2] ?: 0
    }.first()

    override suspend fun getIsReversed(): Boolean = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.UNIT_CONVERTER_REVERSED] ?: false
    }.first()
}