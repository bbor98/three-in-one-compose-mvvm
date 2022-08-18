package com.borabor.threeinonecompose.ui.screen.calculator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.borabor.threeinonecompose.R
import com.borabor.threeinonecompose.util.AutoSizeText
import com.borabor.threeinonecompose.util.ComposableLifecycle

@Composable
fun CalculatorScreen() {
    val viewModel: CalculatorViewModel = hiltViewModel()

    ComposableLifecycle { event ->
        if (event == Lifecycle.Event.ON_PAUSE) viewModel.saveCalculator()
    }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (keypad, result, operationsList) = createRefs()

        OperationsList(
            modifier = Modifier
                .constrainAs(operationsList) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    bottom.linkTo(result.top)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
                .padding(start = 16.dp, end = 16.dp),
            list = viewModel.operationsList
        )
        ResultText(
            modifier = Modifier
                .constrainAs(result) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(keypad.top)
                    width = Dimension.fillToConstraints
                }
                .padding(start = 16.dp, end = 16.dp),
            text = if (viewModel.isError) stringResource(id = R.string.calculator_error) else viewModel.resultText
        )
        Keypad(
            modifier = Modifier
                .constrainAs(ref = keypad) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                }
                .padding(bottom = 8.dp),
            isExpanded = viewModel.isExpanded,
            toggleExpansion = viewModel::toggleExpansion,
            text = viewModel.resultText,
            clear = viewModel::clear,
            delete = viewModel::delete,
            calculate = viewModel::calculate,
            appendValue = { value ->
                viewModel.appendToText(value)
            }
        ) {
            ScientificOperators(
                isSecondary = viewModel.isSecondary,
                toggleSecondary = viewModel::toggleSecondary,
                angleType = viewModel.angleType,
                changeAngleType = viewModel::changeAngleType
            ) { value ->
                viewModel.appendToText(value)
            }
        }
    }
}

@Composable
private fun OperationsList(
    modifier: Modifier,
    list: List<Pair<String, String>>
) {
    val state = rememberLazyListState()

    LaunchedEffect(key1 = list.size) {
        if (list.isNotEmpty()) state.animateScrollToItem(list.lastIndex)
    }

    LazyColumn(
        modifier = modifier,
        state = state,
        verticalArrangement = Arrangement.Bottom
    ) {
        items(list) { operation ->
            SelectionContainer(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)) {
                Column(horizontalAlignment = Alignment.End) {
                    AutoSizeText(
                        text = operation.first,
                        maxFontSize = 24.sp,
                        color = Color.Gray
                    )
                    AutoSizeText(
                        text = operation.second,
                        maxFontSize = 24.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultText(
    modifier: Modifier,
    text: String
) {
    SelectionContainer(modifier = modifier) {
        AutoSizeText(
            text = text,
            maxFontSize = 64.sp,
            modifier = Modifier.height(84.dp)
        )
    }
}

@Composable
private fun Keypad(
    modifier: Modifier,
    isExpanded: Boolean,
    toggleExpansion: () -> Unit,
    text: String,
    clear: () -> Unit,
    delete: () -> Unit,
    calculate: () -> Unit,
    appendValue: (Any) -> Unit,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier) {
        AnimatedVisibility(
            visible = isExpanded,
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 4.dp)
        ) { content() }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CustomButton(
                text = if (text.isEmpty()) "AC" else "C",
                contentColor = Color.Black,
                containerColor = Color.LightGray
            ) { clear() }
            CustomButton(
                text = "⌫",
                contentColor = Color.Black,
                containerColor = Color.LightGray
            ) { delete() }
            CustomButton(text = "%") { appendValue("%") }
            CustomButton(text = "÷") { appendValue("÷") }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CustomButton(
                text = "7",
                contentColor = MaterialTheme.colorScheme.onBackground
            ) { appendValue("7") }
            CustomButton(
                text = "8",
                contentColor = MaterialTheme.colorScheme.onBackground
            ) { appendValue("8") }
            CustomButton(
                text = "9",
                contentColor = MaterialTheme.colorScheme.onBackground
            ) { appendValue("9") }
            CustomButton(text = "×") { appendValue("×") }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CustomButton(
                text = "4",
                contentColor = MaterialTheme.colorScheme.onBackground
            ) { appendValue("4") }
            CustomButton(
                text = "5",
                contentColor = MaterialTheme.colorScheme.onBackground
            ) { appendValue("5") }
            CustomButton(
                text = "6",
                contentColor = MaterialTheme.colorScheme.onBackground
            ) { appendValue("6") }
            CustomButton(text = "-") { appendValue("-") }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CustomButton(
                text = "1",
                contentColor = MaterialTheme.colorScheme.onBackground
            ) { appendValue("1") }
            CustomButton(
                text = "2",
                contentColor = MaterialTheme.colorScheme.onBackground
            ) { appendValue("2") }
            CustomButton(
                text = "3",
                contentColor = MaterialTheme.colorScheme.onBackground
            ) { appendValue("3") }
            CustomButton(text = "+") { appendValue("+") }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CustomButton(
                text = if (isExpanded) "↓" else "↑",
                fontWeight = FontWeight.ExtraBold
            ) { toggleExpansion() }
            CustomButton(
                text = "0",
                contentColor = MaterialTheme.colorScheme.onBackground
            ) { appendValue("0") }
            CustomButton(
                text = ".",
                contentColor = MaterialTheme.colorScheme.onBackground
            ) { appendValue(".") }
            CustomButton(
                text = "=",
                contentColor = MaterialTheme.colorScheme.background,
                containerColor = MaterialTheme.colorScheme.primary
            ) { calculate() }
        }
    }
}

@Composable
private fun ScientificOperators(
    isSecondary: Boolean,
    toggleSecondary: () -> Unit,
    angleType: CalculatorViewModel.AngleType,
    changeAngleType: () -> Unit,
    appendValue: (Any) -> Unit
) {
    Column {
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            CustomButton(
                text = "2nd",
                isEnabled = angleType == CalculatorViewModel.AngleType.DEGREE,
                small = true
            ) { toggleSecondary() }
            CustomButton(
                text = when (angleType) {
                    CalculatorViewModel.AngleType.DEGREE -> "deg"
                    CalculatorViewModel.AngleType.RADIAN -> "rad"
                },
                isEnabled = !isSecondary,
                small = true
            ) { changeAngleType() }
            CustomButton(
                text = if (isSecondary) "csc" else "sin",
                small = true
            ) { appendValue(if (isSecondary) "csc(" else "sin(") }
            CustomButton(
                text = if (isSecondary) "sec" else "cos",
                small = true
            ) { appendValue(if (isSecondary) "sec(" else "cos(") }
            CustomButton(
                text = if (isSecondary) "cot" else "tan",
                small = true
            ) { appendValue(if (isSecondary) "cot(" else "tan(") }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CustomButton(
                text = "log",
                small = true
            ) { appendValue("log10(") }
            CustomButton(
                text = "ln",
                small = true
            ) { appendValue("ln(") }
            CustomButton(
                text = "e",
                small = true
            ) { appendValue("e") }
            CustomButton(
                text = "(",
                small = true
            ) { appendValue("(") }
            CustomButton(
                text = ")",
                small = true
            ) { appendValue(")") }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CustomButton(
                text = "π",
                small = true
            ) { appendValue("π") }
            CustomButton(
                text = "x",
                superscriptText = "y",
                small = true
            ) { appendValue("^") }
            CustomButton(
                text = "√x",
                small = true
            ) { appendValue("√(") }
            CustomButton(
                text = "x!",
                small = true
            ) { appendValue("!") }
            CustomButton(
                text = "x",
                superscriptText = "-1",
                small = true
            ) { appendValue("^(-1)") }
        }
    }
}

@Composable
private fun CustomButton(
    text: String,
    superscriptText: String? = null,
    isEnabled: Boolean = true,
    small: Boolean = false,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    fontWeight: FontWeight = FontWeight.Normal,
    onClick: () -> Unit
) {
    ElevatedButton(
        onClick = { if (isEnabled) onClick() },
        modifier = Modifier
            .width(if (small) 75.dp else 90.dp)
            .height(if (small) 40.dp else 90.dp)
            .padding(4.dp),
        enabled = isEnabled,
        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        val superscriptStyle = SpanStyle(
            baselineShift = BaselineShift.Superscript,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = buildAnnotatedString {
                append(text)
                superscriptText?.let {
                    withStyle(superscriptStyle) {
                        append(it)
                    }
                }
            },
            fontSize = if (small) 15.sp else 30.sp,
            fontWeight = fontWeight,
            textAlign = TextAlign.Center
        )
    }
}