package com.borabor.threeinonecompose.ui.screen.unitconverter

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import com.borabor.threeinonecompose.R
import com.borabor.threeinonecompose.util.AutoSizeText
import com.borabor.threeinonecompose.util.ComposableLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.net.UnknownHostException

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UnitConverterScreen() {
    val viewModel: UnitConverterViewModel = hiltViewModel()

    ComposableLifecycle { event ->
        if (event == Lifecycle.Event.ON_PAUSE) viewModel.saveUnitConverter()
    }

    val unitList = listOf(
        stringResource(id = R.string.unit_angle) to R.drawable.ic_angle_24,
        stringResource(id = R.string.unit_area) to R.drawable.ic_area_24,
        stringResource(id = R.string.unit_currency) to R.drawable.ic_currency_24,
        stringResource(id = R.string.unit_data) to R.drawable.ic_data_24,
        stringResource(id = R.string.unit_energy) to R.drawable.ic_energy_24,
        stringResource(id = R.string.unit_force) to R.drawable.ic_force_24,
        stringResource(id = R.string.unit_length) to R.drawable.ic_length_24,
        stringResource(id = R.string.unit_power) to R.drawable.ic_power_24,
        stringResource(id = R.string.unit_pressure) to R.drawable.ic_pressure_24,
        stringResource(id = R.string.unit_speed) to R.drawable.ic_speed_24,
        stringResource(id = R.string.unit_temperature) to R.drawable.ic_temperature_24,
        stringResource(id = R.string.unit_time) to R.drawable.ic_time_24,
        stringResource(id = R.string.unit_volume) to R.drawable.ic_volume_24,
        stringResource(id = R.string.unit_weight) to R.drawable.ic_weight_24,
    )

    val subunitList = stringArrayResource(
        when (viewModel.selectedUnitIndex) {
            0 -> R.array.subunits_angle
            1 -> R.array.subunits_area
            2 -> R.array.subunits_currency
            3 -> R.array.subunits_data
            4 -> R.array.subunits_energy
            5 -> R.array.subunits_force
            6 -> R.array.subunits_length
            7 -> R.array.subunits_power
            8 -> R.array.subunits_pressure
            9 -> R.array.subunits_speed
            10 -> R.array.subunits_temperature
            11 -> R.array.subunits_time
            12 -> R.array.subunits_volume
            else -> R.array.subunits_weight
        }
    )

    LaunchedEffect(key1 = viewModel.selectedUnitIndex) {
        viewModel.updateMediatorSubunitList(subunitList)
    }

    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()
    var isBackEnabled by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = sheetState.isVisible) {
        isBackEnabled = sheetState.isVisible
    }

    BackHandler(enabled = isBackEnabled) {
        if (sheetState.isVisible) coroutineScope.launch {
            sheetState.hide()
        }
    }

    ModalBottomSheetLayout(
        sheetContent = {
            SheetContent(
                sheetState = sheetState,
                coroutineScope = coroutineScope,
                subunitList = subunitList
            ) { subunitType, index ->
                viewModel.changeSubunit(subunitType, index)
            }
        },
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetBackgroundColor = MaterialTheme.colorScheme.surface
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (units, inputOutput, subunitSelector, keypad, keypadBottom, operators) = createRefs()

            UnitList(
                modifier = Modifier.constrainAs(units) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
                unitList = unitList,
                selectedIndex = viewModel.selectedUnitIndex,
            ) { index ->
                viewModel.updateSelectedUnit(index)
            }
            InputOutputText(
                modifier = Modifier
                    .constrainAs(inputOutput) {
                        start.linkTo(parent.start)
                        top.linkTo(units.bottom)
                        end.linkTo(parent.end)
                        bottom.linkTo(subunitSelector.top)
                        width = Dimension.fillToConstraints
                        verticalChainWeight = 0.5f
                    }
                    .padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
                input = viewModel.inputText,
                output = viewModel.outputText,
                subunitList = subunitList,
                index1 = viewModel.subunitIndex1,
                index2 = viewModel.subunitIndex2,
                isLoading = viewModel.isLoading,
            )
            SubunitSelector(
                modifier = Modifier
                    .constrainAs(subunitSelector) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(keypad.top)
                        width = Dimension.fillToConstraints
                    }
                    .padding(16.dp),
                sheetState = sheetState,
                coroutineScope = coroutineScope,
                subunitList = subunitList,
                index1 = viewModel.subunitIndex1,
                index2 = viewModel.subunitIndex2
            ) { viewModel.reverseConversion() }
            Keypad(
                modifier = Modifier
                    .constrainAs(keypad) {
                        start.linkTo(parent.start)
                        end.linkTo(operators.start)
                        bottom.linkTo(keypadBottom.top)
                        width = Dimension.fillToConstraints
                    }
                    .padding(start = 8.dp)
            ) { value ->
                viewModel.appendToText(value)
            }
            KeypadBottomRow(
                modifier = Modifier
                    .constrainAs(keypadBottom) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                        width = Dimension.fillToConstraints
                    }
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                convert = viewModel::convert
            ) { value ->
                viewModel.appendToText(value)
            }
            Operators(
                modifier = Modifier
                    .constrainAs(operators) {
                        end.linkTo(parent.end)
                        bottom.linkTo(keypadBottom.top)
                    }
                    .padding(end = 8.dp),
                clear = viewModel::clear,
                delete = viewModel::delete
            )
        }

        if (viewModel.error != null) {
            val toastMessage = when (viewModel.error) {
                is UnknownHostException -> stringResource(id = R.string.error_connection)
                is FileNotFoundException -> stringResource(id = R.string.error_not_found)
                else -> stringResource(id = R.string.error_unknown)
            }

            Toast.makeText(LocalContext.current, toastMessage, Toast.LENGTH_SHORT).show()
        }
    }
}

enum class SubunitType {
    LEFT, RIGHT
}

private var subunitType = SubunitType.LEFT

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SheetContent(
    sheetState: ModalBottomSheetState,
    coroutineScope: CoroutineScope,
    subunitList: Array<String>,
    onClick: (SubunitType, Int) -> Unit
) {
    LazyColumn {
        itemsIndexed(subunitList) { index, item ->
            Text(
                text = item.split(",").first(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable {
                        coroutineScope.launch {
                            onClick(subunitType, index)
                            sheetState.hide()
                        }
                    }
            )
        }
    }
}

@Composable
private fun UnitList(
    modifier: Modifier,
    unitList: List<Pair<String, Int>>,
    selectedIndex: Int,
    onClick: (Int) -> Unit
) {
    val state = rememberLazyListState()

    LaunchedEffect(key1 = selectedIndex) {
        state.animateScrollToItem(selectedIndex)
    }

    LazyRow(
        modifier = modifier,
        state = state
    ) {
        itemsIndexed(unitList) { index, unit ->
            UnitListItem(
                text = unit.first,
                imageId = unit.second,
                isSelected = index == selectedIndex
            ) { onClick(index) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UnitListItem(
    text: String,
    imageId: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    ElevatedAssistChip(
        onClick = { onClick() },
        modifier = Modifier.padding(8.dp),
        label = {
            Text(
                text = text,
                modifier = Modifier.padding(end = 8.dp, top = 8.dp, bottom = 8.dp),
                color = if (isSelected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground
            )
        },
        leadingIcon = {
            Icon(
                painter = painterResource(imageId),
                contentDescription = "Chip image",
                modifier = Modifier.size(AssistChipDefaults.IconSize),
                tint = if (isSelected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.primary
            )
        },
        elevation = AssistChipDefaults.elevatedAssistChipElevation(defaultElevation = 2.dp),
        colors = AssistChipDefaults.elevatedAssistChipColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun InputOutputText(
    modifier: Modifier,
    input: String,
    output: String,
    subunitList: Array<String>,
    index1: Int,
    index2: Int,
    isLoading: Boolean
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SelectionContainer(modifier = Modifier.weight(0.75f)) {
                AutoSizeText(
                    text = input,
                    maxFontSize = 36.sp
                )
            }
            AutoSizeText(
                text = subunitList[index1]
                    .substringAfter("(")
                    .substringBefore(")"),
                maxFontSize = 36.sp,
                modifier = Modifier.weight(0.25f)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(modifier = Modifier.weight(0.75f))
            Text(
                text = stringResource(id = R.string.equals_to),
                modifier = Modifier.weight(0.25f),
                fontSize = 14.sp,
                textAlign = TextAlign.End
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isLoading) CircularProgressIndicator()
            else {
                SelectionContainer(modifier = Modifier.weight(0.75f)) {
                    AutoSizeText(
                        text = output,
                        maxFontSize = 36.sp
                    )
                }
            }
            AutoSizeText(
                text = subunitList[index2]
                    .substringAfter("(")
                    .substringBefore(")"),
                maxFontSize = 36.sp,
                modifier = Modifier.weight(0.25f)
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SubunitSelector(
    modifier: Modifier,
    sheetState: ModalBottomSheetState,
    coroutineScope: CoroutineScope,
    subunitList: Array<String>,
    index1: Int,
    index2: Int,
    reverse: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ElevatedButton(
            onClick = {
                coroutineScope.launch {
                    subunitType = SubunitType.LEFT
                    sheetState.show()
                }
            },
            modifier = Modifier.weight(0.4f),
            elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 2.dp),
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.background
            )
        ) {
            Row {
                Text(
                    text = subunitList[index1].substringBefore(" ("),
                    modifier = Modifier.weight(0.75f),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Subunit selector 1",
                    modifier = Modifier.weight(0.25f)
                )
            }
        }
        FloatingActionButton(
            onClick = { reverse() },
            modifier = Modifier.weight(0.2f),
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 2.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_repeat_24),
                contentDescription = "Reverse conversion"
            )
        }
        ElevatedButton(
            onClick = {
                coroutineScope.launch {
                    subunitType = SubunitType.RIGHT
                    sheetState.show()
                }
            },
            modifier = Modifier.weight(0.4f),
            elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 2.dp),
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.background
            )
        ) {
            Row {
                Text(
                    text = subunitList[index2].substringBefore(" ("),
                    modifier = Modifier.weight(0.75f),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Subunit selector 2",
                    modifier = Modifier.weight(0.25f),
                )
            }
        }
    }
}

@Composable
private fun Keypad(
    modifier: Modifier,
    appendValue: (Any) -> Unit
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CustomButton(
                text = "7",
                modifier = Modifier.weight(1f)
            ) { appendValue(7) }
            CustomButton(
                text = "8",
                modifier = Modifier.weight(1f)
            ) { appendValue(8) }
            CustomButton(
                text = "9",
                modifier = Modifier.weight(1f)
            ) { appendValue(9) }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CustomButton(
                text = "4",
                modifier = Modifier.weight(1f)
            ) { appendValue(4) }
            CustomButton(
                text = "5",
                modifier = Modifier.weight(1f)
            ) { appendValue(5) }
            CustomButton(
                text = "6",
                modifier = Modifier.weight(1f)
            ) { appendValue(6) }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CustomButton(
                text = "1",
                modifier = Modifier.weight(1f)
            ) { appendValue(1) }
            CustomButton(
                text = "2",
                modifier = Modifier.weight(1f)
            ) { appendValue(2) }
            CustomButton(
                text = "3",
                modifier = Modifier.weight(1f)
            ) { appendValue(3) }
        }
    }
}

@Composable
private fun KeypadBottomRow(
    modifier: Modifier,
    convert: () -> Unit,
    appendValue: (Any) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        CustomButton(
            text = ".",
            modifier = Modifier.weight(0.25f)
        ) { appendValue(".") }
        CustomButton(
            text = "0",
            modifier = Modifier.weight(0.25f)
        ) { appendValue(0) }
        CustomButton(
            text = "=",
            modifier = Modifier.weight(0.47f),
            contentColor = MaterialTheme.colorScheme.background,
            containerColor = MaterialTheme.colorScheme.primary
        ) { convert() }
    }
}

@Composable
private fun Operators(
    modifier: Modifier,
    clear: () -> Unit,
    delete: () -> Unit
) {
    Column(modifier = modifier) {
        CustomButton(
            text = "AC",
            modifier = Modifier
                .width(90.dp)
                .height(150.dp),
            contentColor = Color.Black,
            containerColor = Color.LightGray
        ) { clear() }
        CustomButton(
            text = "⌫",
            modifier = Modifier
                .width(90.dp)
                .height(150.dp),
            contentColor = Color.Black,
            containerColor = Color.LightGray
        ) { delete() }
    }
}

@Composable
private fun CustomButton(
    text: String,
    modifier: Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    fontWeight: FontWeight = FontWeight.Normal,
    onClick: () -> Unit
) {
    ElevatedButton(
        onClick = { onClick() },
        modifier = modifier
            .height(100.dp)
            .padding(4.dp),
        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            fontSize = 30.sp,
            fontWeight = fontWeight,
            textAlign = TextAlign.Center
        )
    }
}