package com.emma_ea.restaurants

import DummyContent
import com.emma_ea.restaurants.restaurants.data.RestaurantRepository
import com.emma_ea.restaurants.restaurants.domain.GetSortedRestaurantsUseCase
import com.emma_ea.restaurants.restaurants.domain.ToggleRestaurantUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

@ExperimentalCoroutinesApi
class ToggleRestaurantUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private val restaurantRepository = RestaurantRepository(
        FakeApiService(throwException = false),
        FakeRoomDao(),
        dispatcher
    )

    private val getSortedRestaurantRepository =
        GetSortedRestaurantsUseCase(restaurantRepository)

    @Test
    fun toggleRestaurant_isUpdatingFavoriteField() = scope.runTest {
        val useCase = ToggleRestaurantUseCase(
            restaurantRepository,
            getSortedRestaurantRepository
        )

        restaurantRepository.loadRestaurants()
        advanceUntilIdle()

        val restaurants = DummyContent.getDomainResults()
        val targetItem = restaurants[0]
        val isFavorite = targetItem.isFavorite

        val updatedRestaurants = useCase(targetItem.id, isFavorite)
        advanceUntilIdle()

        restaurants[0] = targetItem.copy(isFavorite = !isFavorite)
        assert(updatedRestaurants == restaurants)
    }
}