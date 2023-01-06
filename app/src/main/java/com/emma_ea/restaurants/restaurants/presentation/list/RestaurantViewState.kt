package com.emma_ea.restaurants.restaurants.presentation.list

import com.emma_ea.restaurants.restaurants.domain.Restaurant

data class RestaurantViewState(
    val data: List<Restaurant> = emptyList(),
    val loading: Boolean = false,
    val error: String = ""
)