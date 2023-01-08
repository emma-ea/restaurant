package com.emma_ea.restaurants

import DummyContent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import com.emma_ea.restaurants.restaurants.presentation.Description
import com.emma_ea.restaurants.restaurants.presentation.list.RestaurantScreen
import com.emma_ea.restaurants.restaurants.presentation.list.RestaurantScreenState
import com.emma_ea.restaurants.ui.theme.RestaurantsTheme
import org.junit.Rule
import org.junit.Test

class RestaurantScreenTest {

    @get:Rule
    val testRule: ComposeContentTestRule = createComposeRule()

    @Test
    fun initialState_isRendered() {
        testRule.setContent { 
            RestaurantsTheme {
                val state = RestaurantScreenState(loading = true)
                RestaurantScreen(
                    state = state,
                    requestData = {},
                    onFavoriteClick = {_: Int, _: Boolean -> } ,
                    onItemClick = {}
                )
            }
        }

        testRule.onNodeWithContentDescription(Description.RESTAURANT_LOADING).assertIsDisplayed()
    }

    @Test
    fun stateWithContent_isRendered() {
        val restaurants = DummyContent.getDomainResults()
        testRule.setContent {
            RestaurantsTheme {
                val state = RestaurantScreenState(data = restaurants, loading = false, error = "")
                RestaurantScreen(
                    state = state,
                    requestData = {},
                    onFavoriteClick = {_: Int, _: Boolean -> },
                    onItemClick = {}
                )
            }
        }

        testRule.onNodeWithText(restaurants[0].title).assertIsDisplayed()
        testRule.onNodeWithText(restaurants[0].description).assertIsDisplayed()
        testRule.onNodeWithContentDescription(Description.RESTAURANT_LOADING).assertDoesNotExist()
    }

    @Test
    fun stateWithContent_clickOnItem_isRegistered() {
        val restaurants = DummyContent.getDomainResults()
        val targetRestaurant = restaurants[0]
        testRule.setContent {
            RestaurantsTheme {
                RestaurantScreen(
                    state = RestaurantScreenState(data = restaurants, loading = false, error = ""),
                    requestData = { },
                    onFavoriteClick = {_: Int, _: Boolean -> },
                    onItemClick = { id -> assert(id == targetRestaurant.id) }
                )
            }
        }

        testRule.onNodeWithText(targetRestaurant.title).performClick()
    }

    @Test
    fun errorState_isRendered() {
        val errorMsg = "no internet"
        testRule.setContent {
            RestaurantsTheme {
                RestaurantScreen(
                    state = RestaurantScreenState(data = emptyList(), loading = false, error = errorMsg),
                    requestData = { },
                    onFavoriteClick = {_: Int, _: Boolean -> },
                    onItemClick = { }
                )
            }
        }

        testRule.onNodeWithText(errorMsg).assertIsDisplayed()
        testRule.onNodeWithText("retry", ignoreCase = true).assertIsDisplayed()
        testRule.onNodeWithContentDescription(Description.RESTAURANT_LOADING).assertDoesNotExist()
    }

}