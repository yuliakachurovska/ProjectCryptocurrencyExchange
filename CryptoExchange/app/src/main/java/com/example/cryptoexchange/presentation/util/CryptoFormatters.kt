package com.example.cryptoexchange.presentation.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CryptoFormatters {

    fun formatUsdPrice(price: Double?): String {
        if (price == null) return "Немає даних"
        if (price == 0.0) return "$0.00"

        return when {
            price >= 1.0 -> String.format(Locale.US, "$%,.2f", price)
            price >= 0.01 -> String.format(Locale.US, "$%.4f", price)
            else -> {
                val formatter = DecimalFormat("$0.##########", DecimalFormatSymbols(Locale.US))
                formatter.format(price)
            }
        }
    }

    fun formatTimestamp(millis: Long?): String {
        if (millis == null || millis <= 0L) return "Ще не оновлювалося"
        val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
        return formatter.format(Date(millis))
    }
}