package com.emma_ea.restaurants

class ToggleRestaurantUseCase {

    private val repository = RestaurantRepository()
    private val getSortedRestaurantsUseCase = GetSortedRestaurantsUseCase()

    suspend operator fun invoke(id: Int, oldValue: Boolean): List<Restaurant> {
        val newFav = oldValue.not()
        repository.toggleFavoriteRestaurant(id, newFav)
        return getSortedRestaurantsUseCase()
    }

}