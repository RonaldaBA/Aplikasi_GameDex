package com.example.aplikasi_gamedex.repository

import com.example.aplikasi_gamedex.models.CheapSharkDeal

object FavoritesRepository {

    private val favoritesList = mutableListOf<CheapSharkDeal>()

    fun addFavorite(deal: CheapSharkDeal) {
        if (!favoritesList.any { it.dealID == deal.dealID }) {
            favoritesList.add(deal)
        }
    }

    fun removeFavorite(deal: CheapSharkDeal) {
        favoritesList.removeAll { it.dealID == deal.dealID }
    }

    fun isFavorite(dealID: String?): Boolean {
        if (dealID.isNullOrBlank()) return false
        return favoritesList.any { it.dealID == dealID }
    }

    fun getFavorites(): List<CheapSharkDeal> {
        return favoritesList.toList()
    }
}
