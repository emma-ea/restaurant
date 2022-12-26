package com.emma_ea.restaurants

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class RestaurantViewModel : ViewModel() {
    fun getRestaurants() = dummyRestaurants

    val state = mutableStateOf(dummyRestaurants)

    fun toggleFavorite(id: Int) {
        val restaurants = state.value.toMutableList()
        val itemIndex = restaurants.indexOfFirst { it.id == id }
        val item = restaurants[itemIndex]
        restaurants[itemIndex] = item.copy(isFavorite = !item.isFavorite)
        state.value = restaurants
    }
}