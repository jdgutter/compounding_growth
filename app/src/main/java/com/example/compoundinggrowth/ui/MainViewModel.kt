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
import androidx.fragment.app.Fragment
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
    val dailyStockPrices = MutableLiveData<List<AlphaVantageApi.DailyQuoteResponse>>()
    var transactionList = MutableLiveData<List<Transaction>>()
    var budgetList = MutableLiveData<List<Budget>>()
    var accountsInvestmentsToggle = false
    private var searchTerm = MutableLiveData<String>()

    private val dbHelp = ViewModelDBHelper()

    // Track current authenticated user
    private var currentAuthUser = invalidUser

    var searchTransactions = MediatorLiveData<List<Transaction>>().apply {
        addSource(transactionList) { transactions ->
            val currentSearchTerm = searchTerm.value ?: ""
            postValue(transactions.filter { it.searchFor(currentSearchTerm) })
        }
        addSource(searchTerm) {newSearchTerm ->
            val currentTransactions = transactionList.value ?: emptyList()
            postValue(currentTransactions.filter { it.searchFor(newSearchTerm)} )
        }
    }

    fun getStockQuote(symbolName : String) {
        viewModelScope.launch(Dispatchers.IO) {
            stockQuote.postValue(repository.fetchStockPrice(symbolName))
        }
    }

    fun getDailyStockPrices(symbols : List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            dailyStockPrices.postValue(repository.fetchMultipleDailyStockPrices(symbols))
        }
    }

    fun fetchTransactions(resultListener:()->Unit) {
        dbHelp.fetchTransaction(currentAuthUser) {
            transactionList.postValue(it)
            resultListener.invoke()
        }
    }

    fun fetchBudgets(resultListener:()->Unit) {
        dbHelp.fetchBudget(currentAuthUser) {
            budgetList.postValue(it)
            resultListener.invoke()
        }
    }

    fun observeStockQuote(): LiveData<AlphaVantageApi.GlobalQuoteResponse> {
        return stockQuote
    }

    fun setCurrentAuthUser(user: User) {
        currentAuthUser = user
    }

    fun getCurrentAuthUser() : User {
        return currentAuthUser
    }

    fun setSearchTerm(newSearchTerm: String) {
        searchTerm.value = newSearchTerm
    }

    private fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }

    fun removeTransaction(txn : Transaction) {
        dbHelp.removeTransaction(currentAuthUser, txn) {
            transactionList.postValue(it)
        }
    }

    fun removeViewer(txn : Transaction) {
        txn.viewer = ""
        dbHelp.updateTransaction(currentAuthUser, txn) {
            transactionList.postValue(it)
        }
    }

    fun updateTransaction(transaction : Transaction) {
        dbHelp.updateTransaction(currentAuthUser, transaction) {
            transactionList.postValue(it)
        }
    }

    fun updateBudget(budget : Budget) {
        dbHelp.updateBudget(currentAuthUser, budget) {
            budgetList.postValue(it)
        }
    }

    fun createTransaction(name: String,
                          amount : Double,
                          date : Date,
                          category : String?,
                          viewer : String,
                          stockSymbol : String?,
                          stockPriceAtTransaction : Double?
    ) {
        val currentUser = currentAuthUser
        val transaction = Transaction(
            ownerName = currentUser.email,
            ownerUid = currentUser.uid,
            uuid = generateUUID(),
            name = name,
            amount = amount,
            date = date,
            category = category,
            viewer = viewer,
            stockSymbol = stockSymbol,
            stockPriceAtTransaction = stockPriceAtTransaction,
        )

        dbHelp.createTransaction(currentAuthUser, transaction) {
            transactionList.postValue(it)
        }
    }

    fun createBudget(category: String) {
        val currentUser = currentAuthUser
        val budget = Budget(
            ownerName = currentUser.name,
            ownerUid = currentUser.uid,
            uuid = generateUUID(),
            category = category,
            budgeted = 0.0,
            remaining = 0.0
        )

        dbHelp.createBudget(currentAuthUser, budget) {
            budgetList.postValue(it)
        }
    }

}