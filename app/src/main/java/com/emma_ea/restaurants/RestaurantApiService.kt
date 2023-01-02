package com.emma_ea.restaurants

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RestaurantApiService {
    @GET("restaurants.json")
    suspend fun getRestaurants(): List<Restaurant>

    @GET("restaurants.json?orderBy=\"r_id\"")
    suspend fun getRestaurant(@Query("equalTo") id: Int): Map<String, Restaurant>

}