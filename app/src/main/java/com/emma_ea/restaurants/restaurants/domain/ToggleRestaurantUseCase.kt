package com.emma_ea.restaurants.restaurants.domain

import com.emma_ea.restaurants.restaurants.data.RestaurantRepository

class ToggleRestaurantUseCase {

    private val repository = RestaurantRepository()
    private val getSortedRestaurantsUseCase = GetSortedRestaurantsUseCase()

    suspend operator fun invoke(id: Int, oldValue: Boolean): List<Restaurant> {
        val newFav = oldValue.not()
        repository.toggleFavoriteRestaurant(id, newFav)
        return getSortedRestaurantsUseCase()
    }

}