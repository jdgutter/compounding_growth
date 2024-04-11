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
import com.example.compoundinggrowth.User
import com.example.compoundinggrowth.invalidUser


class MainViewModel : ViewModel() {

    val alphaVantageApi = AlphaVantageApi.create()
    val repository = AlphaVantageRepository(alphaVantageApi)
    val stockQuote = MutableLiveData<AlphaVantageApi.GlobalQuoteResponse>()

    // Track current authenticated user
    private var currentAuthUser = invalidUser

    fun getStockQuote(symbolName : String) {
        viewModelScope.launch(Dispatchers.IO) {
            stockQuote.postValue(repository.fetchStockPrice(symbolName))
        }
    }

    fun observeStockQuote(): LiveData<AlphaVantageApi.GlobalQuoteResponse> {
        return stockQuote
    }

    fun setCurrentAuthUser(user: User) {
        currentAuthUser = user
    }

    //fun getStockTransaction(symbolName : String) : Transaction? {
//
    //    viewModelScope.launch(Dispatchers.IO) {
    //        val transaction = repository.fetchStockPriceAndCreateTransaction(symbolName)
//
    //        if (transaction != null) {
    //    }
    //}

}