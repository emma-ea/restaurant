package com.emma_ea.restaurants

class GetSortedRestaurantsUseCase {

    private val repository = RestaurantRepository()

    suspend operator fun invoke(): List<Restaurant> {
        return repository.getRestaurants().sortedBy { it.title }
    }

}