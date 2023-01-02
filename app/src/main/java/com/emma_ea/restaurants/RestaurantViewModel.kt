package com.emma_ea.restaurants

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import java.net.UnknownHostException

class RestaurantViewModel(private val stateHandle: SavedStateHandle) : ViewModel() {

    private val _state = mutableStateOf(RestaurantViewState())
    val state: State<RestaurantViewState> = _state

    private var restInterface: RestaurantApiService

    private var restaurantsDao = RestaurantDatabase
        .getDaoIntance(RestaurantsApplication.getAppContext())

    private val errorHandler = CoroutineExceptionHandler { _, e ->
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                when (e) {
                    is UnknownHostException,
                        is ConnectException,
                        is HttpException -> {
                        restaurantsDao.getAll().let { cache ->
                            if (cache.isNotEmpty()) _state.value = RestaurantViewState(data = cache)
                            else _state.value = RestaurantViewState(error = "Couldn't find cache. Connect to the internet")
                        }
                        }
                    else -> _state.value = RestaurantViewState(error = e.message ?: "Something went wrong")
                }
            }
        }
        e.printStackTrace()
    }

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://wonder-words-9f366-default-rtdb.firebaseio.com/")
            .build()
        restInterface = retrofit.create(RestaurantApiService::class.java)

        viewModelScope.launch(errorHandler) {
            getRestaurants()
        }
    }

    fun retry() {
        viewModelScope.launch(errorHandler) {
            getRestaurants()
        }
    }

//    private fun getRestaurantsv() {
//        viewModelScope.launch(Dispatchers.IO + errorHandler) {
//            withContext(Dispatchers.Main) {
//                _state.value = RestaurantViewState(loading = true)
//            }
//            val restaurants = restInterface.getRestaurants()
//            restaurantsDao.addAll(restaurants)
//            withContext(Dispatchers.Main) {
//                _state.value = RestaurantViewState(data = restaurants.restoreSelections())
//            }
//        }
//    }

    private suspend fun getRestaurants(): List<Restaurant> {
        return withContext(Dispatchers.Main) {
            _state.value = RestaurantViewState(loading = true)
            val restaurants = restInterface.getRestaurants()
            _state.value = RestaurantViewState(data = restaurants.restoreSelections())
            restaurantsDao.addAll(restaurants)
            return@withContext restaurants
        }
    }

    fun toggleFavorite(id: Int) {
        val restaurants = _state.value.data.toMutableList()
        val itemIndex = restaurants.indexOfFirst { it.id == id }
        val item = restaurants[itemIndex]
        restaurants[itemIndex] = item.copy(isFavorite = !item.isFavorite)
        storeSelection(restaurants[itemIndex])
        _state.value =  RestaurantViewState(data = restaurants)
    }

    private fun storeSelection(item: Restaurant) {
        val savedToggled = stateHandle
            .get<List<Int>?>(FAVORITES)
            .orEmpty()
            .toMutableList()
        if (item.isFavorite)
            savedToggled.add(item.id)
        else
            savedToggled.remove(item.id)
        stateHandle[FAVORITES] = savedToggled
    }

    private fun List<Restaurant>.restoreSelections(): List<Restaurant> {
        stateHandle.get<List<Int>?>(FAVORITES)?.let { selectedIds ->
            val restaurantsMap = this.associateBy { it.id }
            selectedIds.forEach { id ->
                restaurantsMap[id]?.isFavorite = true
            }
            return restaurantsMap.values.toList()
        }
        return this
    }

    companion object {
        const val FAVORITES = "favorites"
    }

}