package com.example.compoundinggrowth.api

import com.example.compoundinggrowth.model.Transaction
import java.util.Date
import java.util.UUID

class AlphaVantageRepository (private val alphaVantageApi: AlphaVantageApi) {

    suspend fun fetchStockPrice(stockSymbol: String): AlphaVantageApi.GlobalQuoteResponse {
        return alphaVantageApi.getStockPrice(symbol = stockSymbol, apiKey = "AQ6ES11K2AUUB2M5")
    }

    suspend fun fetchMultipleDailyStockPrices(stockSymbol: List<String>): List<AlphaVantageApi.DailyQuoteResponse> {
        return stockSymbol.map { symbol ->
            alphaVantageApi.getDailyStockPrices(symbol = symbol, apiKey = "AQ6ES11K2AUUB2M5")
        }
    }
}