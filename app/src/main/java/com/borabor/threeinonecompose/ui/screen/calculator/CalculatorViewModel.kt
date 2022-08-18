package com.borabor.threeinonecompose.ui.screen.calculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.borabor.threeinonecompose.data.CalculatorRepository
import com.borabor.threeinonecompose.util.trimResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.operator.Operator
import javax.inject.Inject

@HiltViewModel
class CalculatorViewModel @Inject constructor(private val repository: CalculatorRepository) : ViewModel() {

    private val _operationsList = mutableStateListOf<Pair<String, String>>()
    val operationsList: List<Pair<String, String>> = _operationsList

    var resultText by mutableStateOf("")
        private set

    var isExpanded by mutableStateOf(false)
        private set

    var isSecondary by mutableStateOf(false)
        private set

    enum class AngleType {
        DEGREE, RADIAN
    }

    var angleType by mutableStateOf(AngleType.DEGREE)
        private set

    var isError by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            repository.apply {
                _operationsList.addAll(getOperationsList())
                resultText = getResultText()
                isExpanded = getIsExpanded()
                isSecondary = getIsSecondary()
                angleType = getAngleType()
            }
        }
    }

    fun appendToText(value: Any) {
        val mainOperators = listOf("+", "-", "×", "÷")

        when (value) {
            in mainOperators -> if (mainOperators.any { it.first() == resultText.lastOrNull() }) {
                resultText = resultText.dropLast(1) + value
                return
            }
            "." -> mainOperators.any {
                if (!resultText.substringAfterLast(it).contains(".")) resultText += "."
                return
            }
        }

        resultText += value
    }

    private val percent = object : Operator("%", 1, true, PRECEDENCE_POWER + 1) {
        override fun apply(vararg args: Double): Double = args[0] / 100
    }

    @Suppress("RedundantUnitExpression")
    private val factorial = object : Operator("!", 1, true, PRECEDENCE_POWER + 1) {
        override fun apply(vararg args: Double): Double {
            val arg = args[0].toInt()
            require(arg.toDouble() == args[0]) {
                isError = true
                Unit
            }
            require(arg >= 0) {
                isError = true
                Unit
            }
            isError = false

            var result = 1.0
            for (i in 1..arg) {
                result *= i.toDouble()
            }
            return result
        }
    }

    fun calculate() {
        try {
            if (resultText.isEmpty()) return

            convertOperators()

            val result = ExpressionBuilder(resultText)
                .operator(percent)
                .operator(factorial)
                .build()
                .evaluate()

            convertOperators(true)

            val operation = resultText
            val operationResult = "= ${result.trimResult()}"
            val item = operation to operationResult
            _operationsList.add(item)

            resultText = result.trimResult()
            isError = false
        } catch (e: Exception) {
            isError = true
        }
    }

    private fun convertOperators(revert: Boolean = false) {
        if (angleType == AngleType.DEGREE) resultText = resultText
            .replace(if (revert) "sin(π/180*" else "sin(", if (revert) "sin(" else "sin(π/180*")
            .replace(if (revert) "cos(π/180*" else "cos(", if (revert) "cos(" else "cos(π/180*")
            .replace(if (revert) "tan(π/180*" else "tan(", if (revert) "tan(" else "tan(π/180*")

        resultText = resultText
            .replace(if (revert) "1/sin" else "csc", if (revert) "csc" else "1/sin")
            .replace(if (revert) "1/cos" else "sec", if (revert) "sec" else "1/cos")
            .replace(if (revert) "1/tan" else "cot", if (revert) "cot" else "1/tan")
            .replace(if (revert) "log" else "ln", if (revert) "ln" else "log")
            .replace(if (revert) "*" else "×", if (revert) "×" else "*")
            .replace(if (revert) "/" else "÷", if (revert) "÷" else "/")
            .replace(if (revert) "sqrt" else "√", if (revert) "√" else "sqrt")
    }

    fun clear() {
        if (isError) isError = false
        if (resultText.isEmpty()) _operationsList.clear() else resultText = ""
    }

    fun delete() {
        resultText = if (isError) "".also { isError = false }
        else resultText.dropLast(1)
    }

    fun toggleExpansion() {
        isExpanded = !isExpanded
    }

    fun toggleSecondary() {
        isSecondary = !isSecondary
    }

    fun changeAngleType() {
        angleType = when (angleType) {
            AngleType.DEGREE -> AngleType.RADIAN
            AngleType.RADIAN -> AngleType.DEGREE
        }
    }

    fun saveCalculator() {
        repository.saveCalculator(
            operationsList = operationsList,
            resultText = resultText,
            isExpanded = isExpanded,
            isSecondary = isSecondary,
            angleType = angleType
        )
    }
}