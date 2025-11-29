package com.example.aplikasi_gamedex.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CheapSharkDeal(
    val internalName: String?,
    val title: String,
    val metacriticLink: String?,
    val dealID: String,
    val storeID: String,
    val gameID: String,
    val salePrice: String,
    val normalPrice: String,
    val isOnSale: String?,
    val savings: String?,
    val steamRatingText: String?,
    val steamRatingPercent: String?,
    val steamRatingCount: String?,
    val releaseDate: Long?,
    val lastChange: Long?,
    val dealRating: String?,
    val thumb: String
) : Parcelable
