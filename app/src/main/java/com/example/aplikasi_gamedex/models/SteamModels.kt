package com.example.aplikasi_gamedex.models

data class SteamAppResponse(
    val success: Boolean = false,
    val data: SteamAppData? = null
)

data class SteamAppData(
    val price_overview: SteamPriceOverview? = null
    // tambahkan fields lain bila perlu
)

data class SteamPriceOverview(
    val currency: String? = null,
    val initial: Int? = null,
    val final: Int? = null,
    val discount_percent: Int? = null
)