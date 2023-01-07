package com.emma_ea.restaurants.restaurants.presentation.list

import com.emma_ea.restaurants.restaurants.domain.Restaurant

data class RestaurantScreenState(
    val data: List<Restaurant> = emptyList(),
    val loading: Boolean = false,
    val error: String = ""
)