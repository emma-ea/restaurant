package com.emma_ea.restaurants.restaurants.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.emma_ea.restaurants.restaurants.presentation.details.RestaurantDetailScreen
import com.emma_ea.restaurants.restaurants.presentation.details.RestaurantDetailScreenState
import com.emma_ea.restaurants.restaurants.presentation.details.RestaurantDetailViewModel
import com.emma_ea.restaurants.restaurants.presentation.list.RestaurantScreen
import com.emma_ea.restaurants.restaurants.presentation.list.RestaurantScreenState
import com.emma_ea.restaurants.restaurants.presentation.list.RestaurantViewModel
import com.emma_ea.restaurants.ui.theme.RestaurantsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RestaurantsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
//                    Greeting("Android")
                    RestaurantApp()
                }
            }
        }
    }
}

@Composable
private fun RestaurantApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "restaurants") {
        composable(route = "restaurants") {
            val viewModel: RestaurantViewModel = hiltViewModel()
            RestaurantScreen(
                viewModel.state.value,
                requestData =  { viewModel.retry() },
                onFavoriteClick =  {id, oldValue -> viewModel.toggleFavorite(id, oldValue) }
            ) { id ->
                navController.navigate("restaurants/$id")
            }
        }
        composable(
            route = "restaurants/{restaurant_id}",
            arguments = listOf(navArgument("restaurant_id"){
                type = NavType.IntType
            }),
            deepLinks = listOf(navDeepLink {
                uriPattern = "www.restaurantapp.details.com/{restaurant_id}"
            })
        ) { navStackEntry ->
            val viewModel: RestaurantDetailViewModel = hiltViewModel()
            val id = navStackEntry.arguments?.getInt("restaurant_id")
            RestaurantDetailScreen(viewModel.state.value)
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    RestaurantsTheme {
        Greeting("Android")
    }
}