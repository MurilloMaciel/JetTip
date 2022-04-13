package com.maciel.murillo.jettip

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maciel.murillo.jettip.ui.theme.JetTipTheme
import com.maciel.murillo.jettip.util.calculateTotalPerPerson
import com.maciel.murillo.jettip.util.calculateTotalTip
import com.maciel.murillo.jettip.util.toTipPercentage
import com.maciel.murillo.jettip.widgets.OutlineInputField
import com.maciel.murillo.jettip.widgets.RoundedIconButton


@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun MainScreen() {
    val totalBillState = remember {
        mutableStateOf(value = "")
    }

    val isValidState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }

    val sliderPositionState = remember {
        mutableStateOf(value = 0F)
    }

    val tipAmountState = remember {
        mutableStateOf(value = 0.0)
    }

    val totalPerPersonState = remember {
        mutableStateOf(value = 0.0)
    }

    val splitByState = remember {
        mutableStateOf(value = 1)
    }

    JetTipTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            Column {
                TopHeader(
                    totalPerPerson = totalPerPersonState.value
                )
                Body(
                    isValid = isValidState,
                    totalBillState = totalBillState,
                    splitByState = splitByState,
                    sliderPositionState = sliderPositionState,
                    tipAmountState = tipAmountState,
                    totalPerPersonState = totalPerPersonState,
                ) { totalBill ->
                    totalBillState.value = totalBill
                }
            }
        }
    }
}

@Composable
private fun TopHeader(
    totalPerPerson: Double = 0.0
) {
    val total = "%.2f".format(totalPerPerson)
    Surface(
        color = Color(color = 0xFFE9D7F7),
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 16.dp)
            .height(150.dp)
            .clip(shape = RoundedCornerShape(size = 12.dp))
    ) {
        Column(
            modifier = Modifier.padding(all = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Total per Person",
                style = MaterialTheme.typography.h5
            )
            Text(
                text = "$$total",
                style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
private fun Body(
    isValid: Boolean,
    totalBillState: MutableState<String>,
    splitByState: MutableState<Int>,
    sliderPositionState: MutableState<Float>,
    tipAmountState: MutableState<Double>,
    totalPerPersonState: MutableState<Double>,
    onValueChanged: (String) -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Surface(
        shape = RoundedCornerShape(size = 8.dp),
        border = BorderStroke(
            width = 1.dp,
            color = Color.LightGray
        ),
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(all = 6.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
        ) {
            OutlineInputField(
                valueState = totalBillState,
                label = "Enter Bill",
                isSingleLine = true,
                keyboardType = KeyboardType.Number,
                onAction = KeyboardActions {
                    if (isValid.not()) return@KeyboardActions
                    onValueChanged(totalBillState.value.trim())
                    keyboardController?.hide()
                }
            )
            AnimatedVisibility(
                visible = isValid
            ) {
                Column {
                    SplitTipSelector(
                        splitByState = splitByState,
                        sliderPositionState = sliderPositionState,
                        totalBillState = totalBillState,
                        totalPerPersonState = totalPerPersonState,
                    )
                    TipPercentage(
                        tipAmountState = tipAmountState,
                    )
                    TipSlider(
                        sliderPositionState = sliderPositionState,
                        totalBillState = totalBillState,
                        tipAmountState = tipAmountState,
                        splitByState = splitByState,
                        totalPerPersonState = totalPerPersonState,
                    )
                }
            }
        }
    }
}

@Composable
private fun SplitTipSelector(
    splitByState: MutableState<Int>,
    sliderPositionState: MutableState<Float>,
    totalBillState: MutableState<String>,
    totalPerPersonState: MutableState<Double>,
) {
    Row(
        modifier = Modifier.padding(all = 4.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = "Split",
            modifier = Modifier.align(
                alignment = Alignment.CenterVertically
            )
        )
        Spacer(modifier = Modifier.width(120.dp))
        Row(
            modifier = Modifier.padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.End
        ) {
            RoundedIconButton(imageVector = Icons.Default.Remove) {
                if (splitByState.value > 1) splitByState.value--
                totalPerPersonState.value = calculateTotalPerPerson(
                    totalBill = totalBillState.value.toDoubleOrNull(),
                    tipPercentage = sliderPositionState.value.toTipPercentage(),
                    splitBy = splitByState.value
                )
            }
            Text(
                text = splitByState.value.toString(),
                modifier = Modifier
                    .align(alignment = Alignment.CenterVertically)
                    .padding(horizontal = 12.dp)
            )
            RoundedIconButton(imageVector = Icons.Default.Add) {
                splitByState.value++
                totalPerPersonState.value = calculateTotalPerPerson(
                    totalBill = totalBillState.value.toDoubleOrNull(),
                    tipPercentage = sliderPositionState.value.toTipPercentage(),
                    splitBy = splitByState.value
                )
            }
        }
    }
}

@Composable
private fun TipPercentage(
    tipAmountState: MutableState<Double>,
) {
    Row(
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Tip",
            modifier = Modifier.align(alignment = Alignment.CenterVertically)
        )
        Spacer(modifier = Modifier.width(200.dp))
        Text(
            text = "$ ${tipAmountState.value}",
            modifier = Modifier.align(alignment = Alignment.CenterVertically)
        )
    }
}

@Composable
private fun TipSlider(
    sliderPositionState: MutableState<Float>,
    totalBillState: MutableState<String>,
    splitByState: MutableState<Int>,
    tipAmountState: MutableState<Double>,
    totalPerPersonState: MutableState<Double>,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
    ) {
        Text(text = "${sliderPositionState.value.toTipPercentage()}%")
        Spacer(modifier = Modifier.height(16.dp))
        Slider(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            value = sliderPositionState.value,
            steps = 5,
            onValueChangeFinished = {},
            onValueChange = { newValue ->
                sliderPositionState.value = newValue
                tipAmountState.value = calculateTotalTip(
                    totalBill = totalBillState.value.toDoubleOrNull(),
                    tipPercentage = sliderPositionState.value.toTipPercentage()
                )
                totalPerPersonState.value = calculateTotalPerPerson(
                    totalBill = totalBillState.value.toDoubleOrNull(),
                    tipPercentage = sliderPositionState.value.toTipPercentage(),
                    splitBy = splitByState.value
                )
            }
        )
    }
}

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Preview(showBackground = true)
@Composable
private fun DefaultPreview() {
    MainScreen()
}

