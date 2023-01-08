package com.emma_ea.restaurants

import DummyContent
import androidx.compose.runtime.ExperimentalComposeApi
import com.emma_ea.restaurants.restaurants.data.RestaurantRepository
import com.emma_ea.restaurants.restaurants.domain.GetInitialRestaurantsUseCase
import com.emma_ea.restaurants.restaurants.domain.GetSortedRestaurantsUseCase
import com.emma_ea.restaurants.restaurants.domain.ToggleRestaurantUseCase
import com.emma_ea.restaurants.restaurants.presentation.list.RestaurantScreenState
import com.emma_ea.restaurants.restaurants.presentation.list.RestaurantViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Test

@ExperimentalCoroutinesApi
class RestaurantViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val scope = TestScope(dispatcher)

    private fun getViewModel(throwException: Boolean = false): RestaurantViewModel {
        val restaurantRepository = RestaurantRepository(FakeApiService(throwException), FakeRoomDao(), dispatcher)
        val  getSortedRestaurantsUseCase = GetSortedRestaurantsUseCase(restaurantRepository)
        val getInitialRestaurantsUseCase = GetInitialRestaurantsUseCase(restaurantRepository, getSortedRestaurantsUseCase)
        val getToggleRestaurantUseCase = ToggleRestaurantUseCase(restaurantRepository, getSortedRestaurantsUseCase)
        return RestaurantViewModel(getInitialRestaurantsUseCase, getToggleRestaurantUseCase, dispatcher)
    }

    @Test
    fun initialState_isProduced() = scope.runTest {
        val viewModel = getViewModel()
        val initialState = viewModel.state.value

        assert(initialState == RestaurantScreenState())
    }

     @Test
    fun stateWithLoadingContent_isProduced() = scope.runTest {
        val testVM = getViewModel()
         // I wanted to find a way of testing the next state when the VM is initialized [loading the data].
         // I assumed forwarding the virtual clock a few millis in time should let me hit the loading state.
         // This hacks seems to work: tested delay time millis: 1, 10, 100, 1000, 2000, 3000
         // It fails when the delay time millis is set above 3000.. I assume this maybe dependent on the
         // hardware and pc the jvm is running on.
         advanceTimeBy(100)
        val currentState = testVM.state.value

        assert(currentState == RestaurantScreenState(loading = true, error = ""))
    }

    @Test
    fun stateWithContent_isProduced() = scope.runTest {
        val testVM = getViewModel()
        advanceUntilIdle()
        val currentState = testVM.state.value
        val restaurants = DummyContent.getDomainResults()

        assert(currentState == RestaurantScreenState(data = restaurants, loading = false))
    }

    @Test
    fun stateWithErrorContent_isProduced() = scope.runTest {
        val testVM = getViewModel(throwException = true)
        advanceUntilIdle()
        val currentState = testVM.state.value

        assert(currentState == RestaurantScreenState(loading = false, error = "test exception"))
    }

}