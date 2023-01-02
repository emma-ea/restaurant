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

class RestaurantViewModel() : ViewModel() {

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
                            else _state.value = RestaurantViewState(error = "Couldn't find data. Connect to the internet")
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

        getRestaurants()
    }

    fun retry() {
        getRestaurants()
    }

    private suspend fun refreshCache() {
        val remoteRestaurants = restInterface.getRestaurants()
        val favoriteRestaurants = restaurantsDao.getAllFavorited()
        restaurantsDao.addAll(remoteRestaurants)
        restaurantsDao.updateAll(favoriteRestaurants.map { PartialRestaurant(it.id, true) })
    }

    private fun getRestaurants() {
        viewModelScope.launch(Dispatchers.IO + errorHandler) {
            withContext(Dispatchers.Main) {
                _state.value = RestaurantViewState(loading = true)
            }
            refreshCache()
            withContext(Dispatchers.Main) {
                _state.value = RestaurantViewState(data = restaurantsDao.getAll())
            }
        }
    }


    fun toggleFavorite(id: Int, oldValue: Boolean) {
        viewModelScope.launch(errorHandler) {
            val updatedRestaurants = toggleFavoriteRestaurant(id, oldValue)
            _state.value = RestaurantViewState(data = updatedRestaurants)
        }
    }

    private suspend fun toggleFavoriteRestaurant(id: Int, oldValue: Boolean) =
        withContext(Dispatchers.IO) {
            restaurantsDao.update(
                PartialRestaurant(id = id, isFavorite = !oldValue)
            )
            restaurantsDao.getAll()
        }



}