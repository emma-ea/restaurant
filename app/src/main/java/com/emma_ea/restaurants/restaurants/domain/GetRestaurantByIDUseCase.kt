package com.emma_ea.restaurants.restaurants.domain

import com.emma_ea.restaurants.restaurants.data.RestaurantRepository

class GetRestaurantByIDUseCase {

    private val repository = RestaurantRepository()

    suspend operator fun invoke(id: Int): Restaurant {
        return repository.getRestaurantById(id)
    }

}