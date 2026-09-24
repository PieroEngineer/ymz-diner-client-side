package com.yummizi.app.core.utils

import java.math.BigDecimal
import java.text.DecimalFormat

/**
 * Formats an Int representing cents into a Soles string (e.g., 1500 -> "15.00").
 */
fun Int.toCurrencyFormat(): String {
    val soles = BigDecimal(this).movePointLeft(2)
    return DecimalFormat("0.00").format(soles)
}