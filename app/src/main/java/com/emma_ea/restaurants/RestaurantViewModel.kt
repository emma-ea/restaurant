package com.emma_ea.restaurants

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

class RestaurantViewModel() : ViewModel() {

    private val repository = RestaurantRepository()

    private val _state = mutableStateOf(RestaurantViewState())

    val state: State<RestaurantViewState> = _state

    private val errorHandler = CoroutineExceptionHandler { _, e ->
        e.printStackTrace()
        _state.value = _state.value.copy(error = e.message ?: "Something went wrong", loading = false)
    }

    init {
        getRestaurants()
    }

    private fun getRestaurants() {
        viewModelScope.launch(errorHandler) {
            _state.value = _state.value.copy(loading = true)
            val restaurants = repository.getAllRestaurants()
            _state.value = _state.value.copy(loading = false, data = restaurants)
        }
    }

    fun toggleFavorite(id: Int, oldValue: Boolean) {
        viewModelScope.launch(errorHandler) {
            val updatedRestaurants = repository.toggleFavoriteRestaurant(id, oldValue)
            _state.value = _state.value.copy(data = updatedRestaurants)
        }
    }

    fun retry() {
        getRestaurants()
    }

}