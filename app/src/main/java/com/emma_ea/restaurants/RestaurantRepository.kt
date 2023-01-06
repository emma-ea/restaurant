package com.emma_ea.restaurants

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import java.net.UnknownHostException

class RestaurantRepository {

    private var restInterface: RestaurantApiService =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://wonder-words-9f366-default-rtdb.firebaseio.com/")
            .build()
            .create(RestaurantApiService::class.java)

    private var restaurantsDao = RestaurantDatabase
        .getDaoIntance(RestaurantsApplication.getAppContext())

    private suspend fun refreshCache() {
        val remoteRestaurants = restInterface.getRestaurants()
        val favoriteRestaurants = restaurantsDao.getAllFavorited()
        restaurantsDao.addAll(remoteRestaurants)
        restaurantsDao.updateAll(favoriteRestaurants.map { PartialRestaurant(it.id, true) })
    }

    suspend fun getAllRestaurants(): List<Restaurant> {
        return withContext(Dispatchers.IO) {
            try {
                refreshCache()
            } catch (e: Exception) {
                handleException(e)
            }
            return@withContext restaurantsDao.getAll()
        }
    }

    suspend fun getRestaurantById(id: Int): Restaurant {
        return withContext(Dispatchers.IO) {
            try {
                restaurantsDao.getRestaurantById(id)
            } catch (e: Exception) {
                throw e
            }
        }
    }

    suspend fun toggleFavoriteRestaurant(id: Int, oldValue: Boolean) =
        withContext(Dispatchers.IO) {
            restaurantsDao.update(PartialRestaurant(id = id, isFavorite = !oldValue))
            restaurantsDao.getAll()
        }

    private suspend fun handleException(e: Exception) {
        when (e) {
            is UnknownHostException,
            is ConnectException,
            is HttpException -> {
                if (restaurantsDao.getAll().isEmpty())
                    throw Exception("Couldn't find data. Connect to the internet")
            }
            else -> throw e
        }
    }

}