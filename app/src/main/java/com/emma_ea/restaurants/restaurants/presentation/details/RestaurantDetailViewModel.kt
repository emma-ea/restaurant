package com.emma_ea.restaurants.restaurants.presentation.details

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emma_ea.restaurants.restaurants.data.RestaurantRepository
import com.emma_ea.restaurants.restaurants.domain.GetRestaurantByIDUseCase
import com.emma_ea.restaurants.restaurants.domain.Restaurant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantDetailViewModel @Inject constructor(
    stateHandler: SavedStateHandle,
    private val getRestaurantByIDUseCase: GetRestaurantByIDUseCase
)  : ViewModel() {

    private val _state = mutableStateOf(RestaurantDetailScreenState())
    val state: State<RestaurantDetailScreenState> = _state

    private val errorHandler = CoroutineExceptionHandler { _, e ->
        e.printStackTrace()
    }

    init {
        val id = stateHandler.get<Int>("restaurant_id") ?: 0

        viewModelScope.launch(errorHandler) {
            val restaurant = getRemoteRestaurant(id)
            _state.value = _state.value.copy(data = restaurant)
        }
    }

    private suspend fun getRemoteRestaurant(id: Int): Restaurant {
        return getRestaurantByIDUseCase(id)
    }

}