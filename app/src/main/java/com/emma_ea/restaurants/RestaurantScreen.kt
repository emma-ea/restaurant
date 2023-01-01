package com.emma_ea.restaurants

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.emma_ea.restaurants.ui.theme.RestaurantsTheme


@Preview(showBackground = true)
@Composable
fun RestaurantPreview() {
    RestaurantsTheme {
        RestaurantScreen()
    }
}

@Composable
fun RestaurantScreen() {
    val vm = viewModel<RestaurantViewModel>()
    val state = vm.state.value

    if (state.loading) {
        Box {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }

    if (state.error.isNotEmpty()) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(state.error)
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = { vm.retry() }) {
                Text("Retry")
            }
        }
    } else {
        LazyColumn(contentPadding = PaddingValues(8.dp)) {
            item { Text(text = "Restaurants Available") }
            items(state.data) { restaurant ->
                RestaurantItem(item = restaurant) { id ->
                    vm.toggleFavorite(id)
                }
            }
        }
    }

}

@Composable
fun RestaurantItem(item: Restaurant, onClick: (id: Int) -> Unit) {
    val icon = if (item.isFavorite)
        Icons.Filled.Favorite
    else
        Icons.Filled.FavoriteBorder

    Card(elevation = 4.dp, modifier = Modifier.padding(8.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            RestaurantIcon(Icons.Filled.Place, Modifier.weight(0.15f))
            RestaurantDetails(item.title, item.description, Modifier.weight(0.7f))
            RestaurantIcon(icon, Modifier.weight(0.15f)) {
                onClick(item.id)
            }
        }
    }
}

@Composable
public fun RestaurantIcon(icon: ImageVector, modifier: Modifier, onClick: () -> Unit = { }) {
    Image(
        imageVector = icon,
        contentDescription = "Restaurant Icon",
        modifier = modifier
            .padding(8.dp)
            .clickable { onClick() }
    )
}

@Composable
public fun RestaurantDetails(
    title: String,
    description: String,
    modifier: Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start
) {
    Column(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment
    ) {
        Text(text = title, style = MaterialTheme.typography.h6)
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = description,
                style = MaterialTheme.typography.body2
            )
        }
    }
}

@Composable
fun FavoriteIcon(modifier: Modifier, icon: ImageVector, onClick: () -> Unit ) {
    Image(
        imageVector = icon,
        contentDescription = "Favorite Restaurant Icon",
        modifier = modifier
            .padding(8.dp)
            .clickable { onClick() }
    )
}