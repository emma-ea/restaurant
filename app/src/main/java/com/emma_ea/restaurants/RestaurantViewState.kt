package com.emma_ea.restaurants

data class RestaurantViewState(
    val data: List<Restaurant>,
    val loading: Boolean,
    val error: String = ""
)