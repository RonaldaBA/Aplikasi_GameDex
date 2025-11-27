package com.example.aplikasi_gamedex.network

import com.example.aplikasi_gamedex.models.CheapSharkDeal
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface CheapSharkAPI {
    @GET("deals")
    suspend fun getDeals(
        @Query("storeID") storeID: Int,
        @Query("pageSize") pageSize: Int = 50 // adjust sesuai kebutuhan
    ): List<CheapSharkDeal>

    companion object {
        private const val BASE = "https://www.cheapshark.com/api/1.0/"

        fun create(): CheapSharkAPI {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(CheapSharkAPI::class.java)
        }
    }
}
