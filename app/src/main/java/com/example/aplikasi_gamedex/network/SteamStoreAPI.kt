package com.example.aplikasi_gamedex.network

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.aplikasi_gamedex.models.SteamAppResponse

interface SteamStoreApi {
    @GET("api/appdetails")
    suspend fun getAppDetails(
        @Query("appids") appid: String,
        @Query("cc") country: String = "ID",
        @Query("l") lang: String = "en"
    ): Map<String, SteamAppResponse>

    companion object {
        fun create(): SteamStoreApi {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://store.steampowered.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(SteamStoreApi::class.java)
        }
    }
}
