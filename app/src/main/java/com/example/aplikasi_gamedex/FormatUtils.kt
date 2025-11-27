package com.example.aplikasi_gamedex

import java.text.NumberFormat
import java.util.Locale
import com.example.aplikasi_gamedex.models.SteamPriceOverview

// convert USD string -> IDR string using static rate (fallback)
fun formatToIDR(usd: String?): String {
    val rate = 16000.0 // adjust or fetch real rate
    val priceUsd = usd?.toDoubleOrNull() ?: 0.0
    val priceIdr = priceUsd * rate
    val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    return formatter.format(priceIdr)
}

// format steam price overview into human-readable currency string
fun formatSteamPrice(overview: SteamPriceOverview, requireContext: android.content.Context): String {
    val currency = overview.currency ?: "IDR"
    val finalVal = overview.final ?: return ""
    // For some currencies Steam returns values in cents (USD/EUR/GBP), for IDR it returns full unit.
    // Heuristic: if currency == "USD" or "EUR" or "GBP", divide by 100. For IDR, use as-is.
    val valueDouble = when (currency.uppercase(Locale.getDefault())) {
        "USD", "EUR", "GBP", "AUD", "CAD" -> finalVal / 100.0
        else -> finalVal.toDouble() // IDR and many others already in whole units
    }

    val nf = when (currency.uppercase(Locale.getDefault())) {
        "IDR" -> NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        "USD" -> NumberFormat.getCurrencyInstance(Locale.US)
        "EUR" -> NumberFormat.getCurrencyInstance(Locale.GERMANY)
        else -> NumberFormat.getCurrencyInstance(Locale.getDefault())
    }

    // ensure currency symbol matches code (some Locales show different)
    return nf.format(valueDouble)
}