package com.borabor.threeinonecompose.ui.screen.unitconverter

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.borabor.threeinonecompose.data.UnitConverterRepository
import com.borabor.threeinonecompose.util.trimResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class UnitConverterViewModel @Inject constructor(private val repository: UnitConverterRepository) : ViewModel() {

    var inputText by mutableStateOf("")
        private set

    var outputText by mutableStateOf("")
        private set

    var selectedUnitIndex by mutableStateOf(0)
        private set

    var subunitIndex1 by mutableStateOf(0)
        private set

    var subunitIndex2 by mutableStateOf(0)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<Exception?>(null)
        private set

    private var mediatorSubunitList = arrayOf<String>()

    private var isReversed = false

    init {
        viewModelScope.launch {
            repository.apply {
                inputText = getInputText()
                outputText = getOutputText()
                selectedUnitIndex = getSelectedUnitIndex()
                subunitIndex1 = getSubunitIndex1()
                subunitIndex2 = getSubunitIndex2()
                this@UnitConverterViewModel.isReversed = getIsReversed()
            }
        }
    }

    fun appendToText(value: Any) {
        when (value) {
            '.' -> if (!inputText.contains('.')) inputText += value
            else -> inputText += value
        }
    }

    fun clear() {
        inputText = ""
        outputText = ""
    }

    fun delete() {
        inputText = inputText.dropLast(1)
    }

    fun updateSelectedUnit(index: Int) {
        error = null

        selectedUnitIndex = index

        subunitIndex1 = 0
        subunitIndex2 = 0

        inputText = ""
        outputText = ""
    }

    fun updateMediatorSubunitList(array: Array<String>) {
        mediatorSubunitList = array
    }

    fun reverseConversion() {
        isReversed = !isReversed
        subunitIndex1 = subunitIndex2.also { subunitIndex2 = subunitIndex1 }
        convert()
    }

    fun changeSubunit(subunitType: SubunitType, index: Int) {
        when (subunitType) {
            SubunitType.LEFT -> subunitIndex1 = index
            SubunitType.RIGHT -> subunitIndex2 = index
        }

        convert()
    }

    fun convert() {
        if (inputText.isNotEmpty()) {
            var result: Double
            val inputValue = inputText.toDouble()

            when (selectedUnitIndex) {
                2 -> { // Currency
                    outputText = ""

                    val fromCurrency = mediatorSubunitList[subunitIndex1].substringAfter("(").substringBefore(")").lowercase()
                    val toCurrency = mediatorSubunitList[subunitIndex2].substringAfter("(").substringBefore(")").lowercase()

                    val api = "https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1/latest/currencies/$fromCurrency/$toCurrency.json"

                    viewModelScope.launch {
                        try {
                            withContext(Dispatchers.IO) {
                                isLoading = true

                                @Suppress("BlockingMethodInNonBlockingContext")
                                val apiResult = URL(api).readText()
                                val jsonObject = JSONObject(apiResult)
                                val conversionRate = jsonObject.getString(toCurrency).toDouble()

                                error = null

                                result = inputValue * conversionRate
                                outputText = result.trimResult()
                            }
                        } catch (e: Exception) {
                            error = e
                        }

                        isLoading = false
                    }
                }
                10 -> { // Temperature
                    val tempType1 = mediatorSubunitList[subunitIndex1].split(",").last().toInt()
                    val tempType2 = mediatorSubunitList[subunitIndex2].split(",").last().toInt()

                    result = when (tempType1) {
                        0 -> { // Celsius
                            when (tempType2) {
                                0 -> inputValue
                                1 -> inputValue * 1.8 + 32
                                2 -> inputValue + 273.15
                                3 -> (inputValue + 273.15) * 1.8
                                else -> inputValue * 0.8
                            }
                        }
                        1 -> { // Fahrenheit
                            when (tempType2) {
                                0 -> (inputValue - 32) / 1.8
                                1 -> inputValue
                                2 -> (inputValue - 32) / 1.8 + 273.15
                                3 -> inputValue + 459.67
                                else -> (inputValue - 32) / 2.25
                            }
                        }
                        2 -> { // Kelvin
                            when (tempType2) {
                                0 -> inputValue - 273.15
                                1 -> 1.8 * (inputValue - 273.15) + 32
                                2 -> inputValue
                                3 -> inputValue * 1.8
                                else -> (inputValue - 273.15) * 0.8
                            }
                        }
                        3 -> { // Rankine
                            when (tempType2) {
                                0 -> (inputValue - 32 - 459.67) / 1.8
                                1 -> inputValue - 459.67
                                2 -> inputValue / 1.8
                                3 -> inputValue
                                else -> (inputValue - 32 - 459.67) / 2.25
                            }
                        }
                        else -> { // Réaumur
                            when (tempType2) {
                                0 -> inputValue * 1.25
                                1 -> inputValue * 2.25 + 32
                                2 -> inputValue * 1.25 + 273.15
                                3 -> inputValue * 2.25 + 32 + 459.67
                                else -> inputValue
                            }
                        }
                    }

                    outputText = result.trimResult()
                }
                else -> { // Other
                    val coefficient1 = mediatorSubunitList[subunitIndex1].split(",").last().toDouble()
                    val coefficient2 = mediatorSubunitList[subunitIndex2].split(",").last().toDouble()

                    result = inputValue * coefficient1 / coefficient2
                    outputText = result.trimResult()
                }
            }
        }
    }

    fun saveUnitConverter() {
        repository.saveUnitConverter(
            inputText = inputText,
            outputText = outputText,
            selectedUnitIndex = selectedUnitIndex,
            subunitIndex1 = subunitIndex1,
            subunitIndex2 = subunitIndex2,
            isReversed = isReversed
        )
    }
}