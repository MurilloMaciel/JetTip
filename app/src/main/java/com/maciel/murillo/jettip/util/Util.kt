package com.maciel.murillo.jettip.util

fun calculateTotalTip(totalBill: Double?, tipPercentage: Int): Double {
    return totalBill?.takeIf { it > 0 }?.let { (it * tipPercentage) / 100 } ?: 0.0
}

fun calculateTotalPerPerson(
    totalBill: Double?,
    splitBy: Int,
    tipPercentage: Int
): Double {
    return totalBill?.takeIf { it > 0 }?.let { totalBillValue ->
        (calculateTotalTip(totalBill, tipPercentage) + totalBill) / splitBy
    } ?: 0.0
}

fun Float.toTipPercentage() = (this * 100).toInt()