package com.example.compoundinggrowth.api

import com.example.compoundinggrowth.model.Transaction
import java.util.Date
import java.util.UUID

class AlphaVantageRepository (private val alphaVantageApi: AlphaVantageApi) {

    private fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }


    suspend fun fetchStockPrice(stockSymbol: String): AlphaVantageApi.GlobalQuoteResponse {

        // TODO: remove hard coded API key
        return alphaVantageApi.getStockPrice(symbol = stockSymbol, apiKey = "1L9E3XHTVAEK40MV")
    }

}