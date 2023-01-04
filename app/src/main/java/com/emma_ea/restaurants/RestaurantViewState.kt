package com.emma_ea.restaurants

data class RestaurantViewState(
    val data: List<Restaurant> = emptyList(),
    val loading: Boolean = false,
    val error: String = ""
)