package com.example.compoundinggrowth.api

import com.example.compoundinggrowth.model.Transaction
import java.util.Date
import java.util.UUID

class AlphaVantageRepository (private val alphaVantageApi: AlphaVantageApi) {

    private fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }

    suspend fun fetchStockPriceAndCreateTransaction(stockSymbol: String): Transaction? {

        // TODO: remove hard coded API key
        val response = alphaVantageApi.getStockPrice(stockSymbol, "1L9E3XHTVAEK40MV")

        return if (response.isSuccessful
            && response.body() != null) {
            val price = response.body()!!.globalQuote.price
            Transaction(
                id = generateUUID(),
                name = "Stock purchase - $stockSymbol",
                amount = price,
                date = Date(),
                category = "Investments",
                stockSymbol = stockSymbol,
                stockPriceAtTransaction = price
            )
        } else {
            null
        }

    }

}