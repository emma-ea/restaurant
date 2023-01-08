package com.emma_ea.restaurants

import DummyContent
import com.emma_ea.restaurants.restaurants.data.RestaurantRepository
import com.emma_ea.restaurants.restaurants.domain.GetSortedRestaurantsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

@ExperimentalCoroutinesApi
class GetSortedRestaurantsUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private val restaurantRepository = RestaurantRepository(
        FakeApiService(throwException = false),
        FakeRoomDao(),
        dispatcher
    )

    @Test
    fun getRestaurants_isSortedByTitle() = scope.runTest {
        val useCase = GetSortedRestaurantsUseCase(restaurantRepository)

        restaurantRepository.loadRestaurants()
        advanceUntilIdle()

        val restaurants = DummyContent.getDomainResults().sortedBy { it.title }
        val targetItem =  restaurants[0]
        val sortedRestaurants = useCase()
        advanceUntilIdle()

        assert(targetItem == sortedRestaurants[0])

    }

}