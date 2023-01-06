package com.emma_ea.restaurants.restaurants.presentation.list

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emma_ea.restaurants.restaurants.domain.GetInitialRestaurantsUseCase
import com.emma_ea.restaurants.restaurants.domain.ToggleRestaurantUseCase
import kotlinx.coroutines.*

class RestaurantViewModel() : ViewModel() {

    private val getInitialRestaurantsUseCase = GetInitialRestaurantsUseCase()
    private val getToggleRestaurantUseCase = ToggleRestaurantUseCase()

    private val _state = mutableStateOf(RestaurantScreenState())

    val state: State<RestaurantScreenState> = _state

    private val errorHandler = CoroutineExceptionHandler { _, e ->
        e.printStackTrace()
        _state.value = _state.value.copy(error = e.message ?: "Something went wrong", loading = false)
    }

    init {
        getRestaurants()
    }

    private fun getRestaurants() {
        viewModelScope.launch(errorHandler) {
            _state.value = _state.value.copy(loading = true, error = "")
            val restaurants = getInitialRestaurantsUseCase()
            _state.value = _state.value.copy(loading = false, data = restaurants)
        }
    }

    fun toggleFavorite(id: Int, oldValue: Boolean) {
        viewModelScope.launch(errorHandler) {
            val updatedRestaurants = getToggleRestaurantUseCase(id, oldValue)
            _state.value = _state.value.copy(data = updatedRestaurants)
        }
    }

    fun retry() {
        getRestaurants()
    }

}