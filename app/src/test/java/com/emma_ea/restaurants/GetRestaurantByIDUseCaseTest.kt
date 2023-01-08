package com.emma_ea.restaurants

import DummyContent
import com.emma_ea.restaurants.restaurants.data.RestaurantRepository
import com.emma_ea.restaurants.restaurants.domain.GetRestaurantByIDUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test


@ExperimentalCoroutinesApi
class GetRestaurantByIDUseCaseTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private val repository = RestaurantRepository(
        FakeApiService(throwException = false),
        FakeRoomDao(),
        dispatcher
    )

    @Test
    fun getRestaurant_matchByCorrectID_returnsTrue() = scope.runTest {
        val useCase = GetRestaurantByIDUseCase(repository)

        repository.loadRestaurants()
        advanceUntilIdle()

        val restaurants = DummyContent.getDomainResults()
        val targetItem = restaurants[1]

        val matchTarget = useCase(1)
        advanceUntilIdle()

        assert(targetItem.title == matchTarget.title)

    }

    @Test
    fun getRestaurant_matchByWrongID_returnsFalse() = scope.runTest {
        val useCase = GetRestaurantByIDUseCase(repository)

        repository.loadRestaurants()
        advanceUntilIdle()

        val restaurants = DummyContent.getDomainResults()
        val targetItem = restaurants[1]

        val matchTarget = useCase(0)
        advanceUntilIdle()

        assert(targetItem.title != matchTarget.title)

    }

}