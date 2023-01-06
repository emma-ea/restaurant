package com.emma_ea.restaurants.restaurants.data

import com.emma_ea.restaurants.restaurants.domain.Restaurant
import com.emma_ea.restaurants.RestaurantsApplication
import com.emma_ea.restaurants.restaurants.data.local.LocalRestaurant
import com.emma_ea.restaurants.restaurants.data.local.PartialLocalRestaurant
import com.emma_ea.restaurants.restaurants.data.local.RestaurantDatabase
import com.emma_ea.restaurants.restaurants.data.remote.RestaurantApiService
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
        restaurantsDao.addAll(remoteRestaurants.map {
            LocalRestaurant(
                it.id,
                it.title,
                it.description,
                false,
            )
        })
        restaurantsDao.updateAll(favoriteRestaurants.map { PartialLocalRestaurant(it.id, true) })
    }

    suspend fun loadRestaurants() {
        withContext(Dispatchers.IO) {
            try {
                refreshCache()
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    suspend fun getRestaurants(): List<Restaurant> {
        return withContext(Dispatchers.IO) {
            return@withContext restaurantsDao.getAll()
                .map {
                    Restaurant(it.id, it.title, it.description, it.isFavorite)
                }
        }
    }

    suspend fun getRestaurantById(id: Int): Restaurant {
        return withContext(Dispatchers.IO) {
            try {
                restaurantsDao.getRestaurantById(id).let {
                    Restaurant(it.id, it.title, it.description)
                }
            } catch (e: Exception) {
                throw e
            }
        }
    }

    suspend fun getRemoteRestaurantById(id: Int): Restaurant {
        return withContext(Dispatchers.IO) {
            try {
                val response = restInterface.getRestaurant(id)
                response.values.first().let {
                    Restaurant(it.id, it.title, it.description)
                }
            } catch (e: Exception) {
                throw e
            }
        }
    }

    suspend fun toggleFavoriteRestaurant(id: Int, value: Boolean) =
        withContext(Dispatchers.IO) {
            restaurantsDao.update(PartialLocalRestaurant(id = id, isFavorite = value))
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