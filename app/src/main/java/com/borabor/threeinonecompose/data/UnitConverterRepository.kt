package com.borabor.threeinonecompose.data

interface UnitConverterRepository {
    fun saveUnitConverter(inputText: String, outputText: String, selectedUnitIndex: Int, subunitIndex1: Int, subunitIndex2: Int, isReversed: Boolean)
    suspend fun getInputText(): String
    suspend fun getOutputText(): String
    suspend fun getSelectedUnitIndex(): Int
    suspend fun getSubunitIndex1(): Int
    suspend fun getSubunitIndex2(): Int
    suspend fun getIsReversed(): Boolean
}