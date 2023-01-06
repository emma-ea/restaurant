package com.emma_ea.restaurants.restaurants.domain

import com.emma_ea.restaurants.restaurants.data.RestaurantRepository

class GetInitialRestaurantsUseCase {

    private val repository = RestaurantRepository()
    private val getSortedRestaurantsUseCase = GetSortedRestaurantsUseCase()

    suspend operator fun invoke(): List<Restaurant> {
        repository.loadRestaurants()
        return getSortedRestaurantsUseCase()
    }

}