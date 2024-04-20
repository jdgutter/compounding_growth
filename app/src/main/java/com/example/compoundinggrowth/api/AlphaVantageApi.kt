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

    @GET("query")
    suspend fun getDailyStockPrices(
        @Query("function") function: String ="TIME_SERIES_DAILY",
        @Query("symbol") symbol: String,
        @Query("apikey") apiKey: String
    ) : DailyQuoteResponse


    data class DailyQuoteResponse(
        @SerializedName("Meta Data")
        val metaData: MetaData,

        @SerializedName("Time Series (Daily)")
        val timeSeriesDaily: Map<String, DailyData>
    )

    data class MetaData(
        @SerializedName("1. Information")
        val information: String,

        @SerializedName("2. Symbol")
        val symbol: String,

        @SerializedName("3. Last Refreshed")
        val lastRefreshed: String,

        @SerializedName("4. Output Size")
        val outputSize: String,

        @SerializedName("5. Time Zone")
        val timeZone: String
    )

    data class DailyData(
        @SerializedName("1. open")
        val open: String,

        @SerializedName("2. high")
        val high: String,

        @SerializedName("3. low")
        val low: String,

        @SerializedName("4. close")
        val close: String,

        @SerializedName("5. volume")
        val volume: String
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