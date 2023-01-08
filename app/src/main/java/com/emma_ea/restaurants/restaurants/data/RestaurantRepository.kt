package com.emma_ea.restaurants.restaurants.data

import com.emma_ea.restaurants.restaurants.domain.Restaurant
import com.emma_ea.restaurants.RestaurantsApplication
import com.emma_ea.restaurants.restaurants.data.di.IoDispatcher
import com.emma_ea.restaurants.restaurants.data.local.LocalRestaurant
import com.emma_ea.restaurants.restaurants.data.local.PartialLocalRestaurant
import com.emma_ea.restaurants.restaurants.data.local.RestaurantDatabase
import com.emma_ea.restaurants.restaurants.data.local.RestaurantsDao
import com.emma_ea.restaurants.restaurants.data.remote.RestaurantApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RestaurantRepository @Inject constructor(
    private val restInterface: RestaurantApiService,
    private val restaurantsDao: RestaurantsDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) {

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
        withContext(dispatcher) {
            try {
                refreshCache()
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    suspend fun getRestaurants(): List<Restaurant> {
        return withContext(dispatcher) {
            return@withContext restaurantsDao.getAll()
                .map {
                    Restaurant(it.id, it.title, it.description, it.isFavorite)
                }
        }
    }

    suspend fun getRestaurantById(id: Int): Restaurant {
        return withContext(dispatcher) {
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
        return withContext(dispatcher) {
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
        withContext(dispatcher) {
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