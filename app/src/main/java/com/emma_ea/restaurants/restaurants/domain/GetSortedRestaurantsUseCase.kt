package com.emma_ea.restaurants.restaurants.domain

import com.emma_ea.restaurants.restaurants.data.RestaurantRepository
import javax.inject.Inject

class GetSortedRestaurantsUseCase @Inject constructor(
    private val repository: RestaurantRepository
) {

    suspend operator fun invoke(): List<Restaurant> {
        return repository.getRestaurants().sortedBy { it.title }
    }

}