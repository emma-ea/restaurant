package com.emma_ea.restaurants

import com.emma_ea.restaurants.restaurants.data.local.LocalRestaurant
import com.emma_ea.restaurants.restaurants.data.local.PartialLocalRestaurant
import com.emma_ea.restaurants.restaurants.data.local.RestaurantsDao
import kotlinx.coroutines.delay

class FakeRoomDao : RestaurantsDao {

    private var restaurants = HashMap<Int, LocalRestaurant>()

    override suspend fun getAll(): List<LocalRestaurant> {
        delay(1000)
        return restaurants.values.toList()
    }

    override suspend fun addAll(restaurants: List<LocalRestaurant>) {
        restaurants.forEach {
            this.restaurants[it.id] = it
        }
    }

    override suspend fun update(partialLocalRestaurant: PartialLocalRestaurant) {
        delay(1000)
        updateRestaurant(partialLocalRestaurant)
    }

    override suspend fun updateAll(partialLocalRestaurant: List<PartialLocalRestaurant>) {
        delay(1000)
        partialLocalRestaurant.forEach {
            updateRestaurant(it)
        }
    }

    override suspend fun getAllFavorited(): List<LocalRestaurant> {
        return restaurants.values.toList().filter { it.isFavorite }
    }

    override suspend fun getRestaurantById(id: Int): LocalRestaurant {
        return restaurants.values.toList().first { id == it.id }
    }

    private fun updateRestaurant(partialLocalRestaurant: PartialLocalRestaurant) {
        val restaurant = this.restaurants[partialLocalRestaurant.id]
        if (restaurant != null) {
            this.restaurants[partialLocalRestaurant.id] =
                restaurant.copy(isFavorite = partialLocalRestaurant.isFavorite)
        }
    }
}