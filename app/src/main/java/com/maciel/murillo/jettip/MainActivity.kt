package com.maciel.murillo.jettip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.runtime.*
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
import com.maciel.murillo.jettip.components.OutlineInputField
import com.maciel.murillo.jettip.components.RoundedIconButton
import com.maciel.murillo.jettip.ui.theme.JetTipTheme

@ExperimentalComposeUiApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun MyApp() {
    val totalBillState = remember {
        mutableStateOf(value = "")
    }
//    val isValidState = remember(totalBillState.value) {
//        totalBillState.value.trim().isNotEmpty()
//    }
    val isValidState = remember(totalBillState.value) {
        true
    }

    val sliderPositionState = remember {
        mutableStateOf(value = 0F)
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
                TopHeader(totalPerPerson = totalBillState.value.takeIf {it.isNotBlank()}?.toDouble() ?: 0.0)
                Body(
                    isValid = isValidState,
                    totalBillState = totalBillState,
                    splitByState = splitByState,
                    sliderPositionState = sliderPositionState
                ) { totalBill ->
                    totalBillState.value = totalBill
                }
            }
        }
    }
}

@Composable
fun TopHeader(totalPerPerson: Double = 0.0) {
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

@ExperimentalComposeUiApi
@Composable
fun Body(
    isValid: Boolean,
    totalBillState: MutableState<String>,
    splitByState: MutableState<Int>,
    sliderPositionState: MutableState<Float>,
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
            .padding(all = 2.dp)
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
            SplitTipSelector(
                isValid = isValid,
                splitByState = splitByState
            )
            TipPercentage(
                isValid = isValid
            )
            TipSlider(
                sliderPositionState = sliderPositionState
            )
        }
    }
}

@Composable
fun SplitTipSelector(
    isValid: Boolean,
    splitByState: MutableState<Int>,
) {
    if (isValid) {
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

                }
                Text(
                    text = "0",
                    modifier = Modifier
                        .align(alignment = Alignment.CenterVertically)
                        .padding(horizontal = 12.dp)
                )
                RoundedIconButton(imageVector = Icons.Default.Add) {

                }
            }
        }
    } else {
        Box() {}
    }
}

@Composable
fun TipPercentage(
    isValid: Boolean,
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
            text = "$33.00",
            modifier = Modifier.align(alignment = Alignment.CenterVertically)
        )
    }
}

@Composable
fun TipSlider(
    sliderPositionState: MutableState<Float>
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
    ) {
        Text(text = "33%")
        Spacer(modifier = Modifier.height(16.dp))
        Slider(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp),
            value = sliderPositionState.value,
            steps = 5,
            onValueChangeFinished = {},
            onValueChange = { newValue ->
                sliderPositionState.value = newValue
            }
        )
    }
}

@ExperimentalComposeUiApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApp()
}