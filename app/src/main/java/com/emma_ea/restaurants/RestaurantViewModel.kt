package com.emma_ea.restaurants

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

class RestaurantViewModel() : ViewModel() {

    private val _state = mutableStateOf(
        RestaurantViewState(
            data = emptyList(),
            loading = true,
        )
    )

    val state: State<RestaurantViewState> = _state

    private val repository = RestaurantRepository()

    init {
        getRestaurants()
    }

    private val errorHandler = CoroutineExceptionHandler { _, e ->
        e.printStackTrace()
        _state.value = _state.value.copy(error = e.message ?: "Something went wrong", loading = false)
    }

    private fun getRestaurants() {
        viewModelScope.launch(errorHandler) {
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