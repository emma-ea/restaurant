package com.emma_ea.restaurants.restaurants.domain

import com.emma_ea.restaurants.restaurants.data.RestaurantRepository

class GetSortedRestaurantsUseCase {

    private val repository = RestaurantRepository()

    suspend operator fun invoke(): List<Restaurant> {
        return repository.getRestaurants().sortedBy { it.title }
    }

}