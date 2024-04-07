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


class MainViewModel : ViewModel() {

    val alphaVantageApi = AlphaVantageApi.create()
    val repository = AlphaVantageRepository(alphaVantageApi)
    val stockQuote = MutableLiveData<AlphaVantageApi.GlobalQuoteResponse>()

    fun getStockTransaction(symbolName : String) : Transaction? {

        viewModelScope.launch(Dispatchers.IO) {
            val transaction = repository.fetchStockPriceAndCreateTransaction(symbolName)

            if (transaction != null) {
        }
    }
    /*
    private var title = MutableLiveData<String>()
    private var searchTerm = MutableLiveData<String>()
    private var subreddit = MutableLiveData<String>().apply {
        value = "aww"
    }
    private var actionBarBinding : ActionBarBinding? = null
    val redditApi = RedditApi.create()
    val repository = RedditPostRepository(redditApi)
    private var favorites = MutableLiveData<MutableList<RedditPost>>().apply {
        value = mutableListOf()
    }

    // XXX Write me, api, repository, favorites
    // netSubreddits fetches the list of subreddits
    // We only do this once, so technically it does not need to be
    // MutableLiveData, or even really LiveData.  But maybe in the future
    // we will refetch it.

    private var netSubreddits = MutableLiveData<List<RedditPost>>().apply{
        // XXX Write me, viewModelScope.launch getSubreddits()
        viewModelScope.launch(Dispatchers.IO) {
            postValue(repository.getSubreddits())
        }
    }

    // netPosts fetches the posts for the current subreddit, when that
    // changes
    private var netPosts = MediatorLiveData<List<RedditPost>>().apply {
        addSource(subreddit) { subreddit: String ->
            Log.d("repoPosts", subreddit)
            // XXX Write me, viewModelScope.launch getPosts
            viewModelScope.launch(Dispatchers.IO) {
                postValue(repository.getPosts(subreddit))
            }
        }
    }
    // XXX Write me MediatorLiveData searchSubreddit, searchFavorites
    // searchPosts
    var searchPosts = MediatorLiveData<List<RedditPost>>().apply {
        addSource(netPosts) { redditPosts ->
            val currentSearchTerm = searchTerm.value ?: ""
            postValue(redditPosts.filter { it.searchFor(currentSearchTerm) })
        }
        addSource(searchTerm) {newSearchTerm ->
            val currentPosts = netPosts.value ?: emptyList()
            postValue(currentPosts.filter { it.searchFor(newSearchTerm)} )
        }
    }

    var searchFavorites = MediatorLiveData<List<RedditPost>>().apply {
        addSource(favorites) { redditPosts ->
            val currentSearchTerm = searchTerm.value ?: ""
            postValue(redditPosts.filter { it.searchFor(currentSearchTerm) })
        }
        addSource(searchTerm) {newSearchTerm ->
            val currentPosts = favorites.value ?: emptyList()
            postValue(currentPosts.filter { it.searchFor(newSearchTerm)} )
        }
    }

    var searchSubreddit = MediatorLiveData<List<RedditPost>>().apply {
        addSource(netSubreddits) { redditPosts ->
            val currentSearchTerm = searchTerm.value ?: ""
            postValue(redditPosts.filter { it.searchFor(currentSearchTerm) })
        }
        addSource(searchTerm) {newSearchTerm ->
            val currentPosts = netSubreddits.value ?: emptyList()
            postValue(currentPosts.filter { it.searchFor(newSearchTerm)} )
        }
    }

    // Looks pointless, but if LiveData is set up properly, it will fetch posts
    // from the network
    fun repoFetch() {
        val fetch = subreddit.value
        subreddit.value = fetch
    }

    fun observeTitle(): LiveData<String> {
        return title
    }
    fun setTitle(newTitle: String) {
        title.value = newTitle
    }

    fun setSearchTerm(newSearchTerm: String) {
        searchTerm.value = newSearchTerm
    }

    fun setSubReddit(newSubReddit: String) {
        subreddit.value = newSubReddit
    }
    
    fun observeSubreddit(): LiveData<String> {
        return subreddit
    }

    // ONLY call this from OnePostFragment, otherwise you will have problems.
    fun observeSearchTerm(): LiveData<String> {
        return searchTerm
    }

    /////////////////////////
    // Action bar
    fun initActionBarBinding(it: ActionBarBinding) {
        // XXX Write me, one liner
        actionBarBinding = it
    }
    fun hideActionBarFavorites() {
        // XXX Write me, one liner
        actionBarBinding?.actionFavorite?.visibility = View.GONE
    }
    fun showActionBarFavorites() {
        // XXX Write me, one liner
        actionBarBinding?.actionFavorite?.visibility = View.VISIBLE
    }

    // XXX Write me, set, observe, deal with favorites
    fun addToFavorites(redditPost: RedditPost) {
        val newFavoriteList = favorites.value?.toMutableList()
        newFavoriteList?.add(redditPost)
        favorites.value = newFavoriteList
    }

    // XXX Write me, set, observe, deal with favorites
    fun removeFromFavorites(redditPost: RedditPost) {
        val newFavoriteList = favorites.value?.toMutableList()
        newFavoriteList?.remove(redditPost)
        favorites.value = newFavoriteList
    }

    fun isRedditPostInFavorites(redditPost : RedditPost) : Boolean {
        return (favorites.value?.contains(redditPost) == true)
    }
    */
}