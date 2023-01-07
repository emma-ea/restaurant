package com.emma_ea.restaurants.restaurants.domain

import com.emma_ea.restaurants.restaurants.data.RestaurantRepository
import javax.inject.Inject

class GetRestaurantByIDUseCase @Inject constructor(
    private val repository: RestaurantRepository
) {

    suspend operator fun invoke(id: Int): Restaurant {
        return repository.getRestaurantById(id)
    }

}