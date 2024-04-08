package com.example.compoundinggrowth.api

import com.google.gson.annotations.SerializedName
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

    @GET("query")
    suspend fun getStockPrice(
        @Query("function") function: String ="GLOBAL_QUOTE",
        @Query("symbol") symbol: String,
        @Query("apikey") apiKey: String
    ) : GlobalQuoteResponse


    data class GlobalQuoteResponse (

        @SerializedName("Global Quote")
        val globalQuote: GlobalQuote
    )

    data class GlobalQuote(

        @SerializedName("01. symbol")
        val symbol: String,

        @SerializedName("02. open")
        val open: String,

        @SerializedName("03. high")
        val high: String,

        @SerializedName("04. low")
        val low: String,

        @SerializedName("05. price")
        val price: String,

        @SerializedName("06. volume")
        val volume: String,

        @SerializedName("07. latest trading day")
        val latestTradingDay: String,

        @SerializedName("08. previous close")
        val previousClose: String,

        @SerializedName("09. change")
        val change: String,

        @SerializedName("10. change percent")
        val changePercent: String,
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
                    this.level = HttpLoggingInterceptor.Level.BODY
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