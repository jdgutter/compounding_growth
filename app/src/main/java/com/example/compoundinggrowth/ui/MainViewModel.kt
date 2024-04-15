package com.example.compoundinggrowth.ui


import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compoundinggrowth.api.AlphaVantageApi
import com.example.compoundinggrowth.api.AlphaVantageRepository
import com.example.compoundinggrowth.model.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.compoundinggrowth.AuthUser
import com.example.compoundinggrowth.R
import com.example.compoundinggrowth.User
import com.example.compoundinggrowth.ViewModelDBHelper
import com.example.compoundinggrowth.invalidUser
import com.example.compoundinggrowth.model.Budget
import java.util.Date
import java.util.UUID


class MainViewModel : ViewModel() {

    val alphaVantageApi = AlphaVantageApi.create()
    val repository = AlphaVantageRepository(alphaVantageApi)
    val stockQuote = MutableLiveData<AlphaVantageApi.GlobalQuoteResponse>()
    var transactionList = MutableLiveData<List<Transaction>>()
    var budgetList = MutableLiveData<List<Budget>>()
    var accountsInvestmentsToggle = false

    private val dbHelp = ViewModelDBHelper()

    // Track current authenticated user
    private var currentAuthUser = invalidUser

    fun getStockQuote(symbolName : String) {
        viewModelScope.launch(Dispatchers.IO) {
            stockQuote.postValue(repository.fetchStockPrice(symbolName))
        }
    }

    fun fetchTransactions(resultListener:()->Unit) {
        dbHelp.fetchTransaction() {
            transactionList.postValue(it)
            resultListener.invoke()
        }
    }

    fun observeStockQuote(): LiveData<AlphaVantageApi.GlobalQuoteResponse> {
        return stockQuote
    }

    fun setCurrentAuthUser(user: User) {
        currentAuthUser = user
    }

    private fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }

    fun removeTransaction(txn : Transaction) {
        dbHelp.removeTransaction(txn) {
            transactionList.postValue(it)
        }
    }

    fun createTransaction(name: String,
                          amount : Double,
                          date : Date,
                          category : String?,
                          stockSymbol : String?,
                          stockPriceAtTransaction : Double?
    ) {
        val currentUser = currentAuthUser
        val transaction = Transaction(
            ownerName = currentUser.name,
            ownerUid = currentUser.uid,
            uuid = generateUUID(),
            name = name,
            amount = amount,
            date = date,
            category = category,
            stockSymbol = stockSymbol,
            stockPriceAtTransaction = stockPriceAtTransaction,
        )

        dbHelp.createTransaction(transaction) {
            transactionList.postValue(it)
        }
    }

}