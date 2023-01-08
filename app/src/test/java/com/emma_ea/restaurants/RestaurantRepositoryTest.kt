package com.emma_ea.restaurants

import DummyContent
import com.emma_ea.restaurants.FakeApiService
import com.emma_ea.restaurants.FakeRoomDao
import com.emma_ea.restaurants.restaurants.data.RestaurantRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

@ExperimentalCoroutinesApi
internal class RestaurantRepositoryTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private val remoteFakeApi = FakeApiService(throwException = false)
    private val fakeDao = FakeRoomDao()

    private val repository = RestaurantRepository(remoteFakeApi, fakeDao, dispatcher)

    @Test
    fun getRestaurantsFromRoom_containsDummy() = scope.runTest {

        repository.loadRestaurants()
        advanceUntilIdle()

        val roomRestaurants = repository.getRestaurants()
        advanceUntilIdle()

        val dummy = DummyContent.getDomainResults()

        assert(roomRestaurants.first().title == dummy.first().title)
    }



}