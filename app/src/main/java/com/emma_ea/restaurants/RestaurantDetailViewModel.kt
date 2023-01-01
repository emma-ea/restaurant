package com.emma_ea.restaurants

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RestaurantDetailViewModel()  : ViewModel() {

    private var restInteface: RestaurantApiService
    val state = mutableStateOf<Restaurant?>(null)

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://wonder-words-9f366-default-rtdb.firebaseio.com/")
            .build()

        restInteface = retrofit.create(RestaurantApiService::class.java)

        viewModelScope.launch {
            val restaurant = getRemoteRestaurant(2)
            state.value = restaurant
        }
    }

    private suspend fun getRemoteRestaurant(id: Int): Restaurant {
        return withContext(Dispatchers.IO) {
            val responseMap = restInteface.getRestaurant(id)
            return@withContext responseMap.values.first()
        }
    }

}