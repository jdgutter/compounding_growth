package com.example.compoundinggrowth.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response

interface AlphaVantageApi {

    @GET("query?function=GLOBAL_QUOTE")
    fun getStockPrice(
        @Query("symbol") symbol: String,
        @Query("apikey") apiKey: String
    ) : Response<GlobalQuoteResponse>


    data class GlobalQuoteResponse (
        val globalQuote: GlobalQuote
    )

    data class GlobalQuote(
        val symbol: String,
        val price: Double
    )

    companion object {
        var httpurl = HttpUrl.Builder()
            .scheme("https")
            .host("www.alphavantage.co")
            .build()

        fun create(): AlphaVantageApi = create(httpurl)

        private fun create(httpUrl: HttpUrl): AlphaVantageApi {
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    this.level = HttpLoggingInterceptor.Level.BASIC
                })
                .build()

            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AlphaVantageApi::class.java)
        }
    }
}