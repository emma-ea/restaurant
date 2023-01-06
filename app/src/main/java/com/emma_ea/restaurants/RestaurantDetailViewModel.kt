package com.emma_ea.restaurants

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RestaurantDetailViewModel(
    private val stateHandler: SavedStateHandle
)  : ViewModel() {

    private val repository = RestaurantRepository()

    val _state = mutableStateOf<Restaurant?>(null)
    val state: State<Restaurant?> = _state

    private val errorHandler = CoroutineExceptionHandler { _, e ->
        e.printStackTrace()
    }

    init {
        val id = stateHandler.get<Int>("restaurant_id") ?: 0

        viewModelScope.launch(errorHandler) {
            val restaurant = getRemoteRestaurant(id)
            _state.value = restaurant
        }
    }

    private suspend fun getRemoteRestaurant(id: Int): Restaurant {
        return repository.getRestaurantById(id)
    }

}