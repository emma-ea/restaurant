package com.emma_ea.restaurants.restaurants.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [LocalRestaurant::class],
    version = 3,
    exportSchema = false
)
abstract class RestaurantDatabase : RoomDatabase() {

    abstract val dao: RestaurantsDao

}