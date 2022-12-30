package com.emma_ea.restaurants

import retrofit2.Call
import retrofit2.http.GET

interface RestaurantApiService {
    @GET("restaurants.json")
    suspend fun getRestaurants(): List<Restaurant>

}