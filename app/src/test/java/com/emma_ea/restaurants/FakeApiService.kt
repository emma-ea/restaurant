package com.emma_ea.restaurants

import DummyContent
import com.emma_ea.restaurants.restaurants.data.remote.RemoteRestaurant
import com.emma_ea.restaurants.restaurants.data.remote.RestaurantApiService
import kotlinx.coroutines.delay

class FakeApiService : RestaurantApiService {

    override suspend fun getRestaurants(): List<RemoteRestaurant> {
        delay(1000)
        return DummyContent.getRemoteRestaurants()
    }

    override suspend fun getRestaurant(id: Int): Map<String, RemoteRestaurant> {
        delay(1000)
        val remoteRestaurant = DummyContent.getRemoteRestaurants().associateBy { it.id.toString() }
        return remoteRestaurant.filter { it.key == id.toString() }
    }
}