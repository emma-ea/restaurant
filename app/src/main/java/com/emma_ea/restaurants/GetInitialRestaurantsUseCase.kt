package com.emma_ea.restaurants

class GetInitialRestaurantsUseCase {

    private val repository = RestaurantRepository()
    private val getSortedRestaurantsUseCase = GetSortedRestaurantsUseCase()

    suspend operator fun invoke(): List<Restaurant> {
        repository.loadRestaurants()
        return getSortedRestaurantsUseCase()
    }

}