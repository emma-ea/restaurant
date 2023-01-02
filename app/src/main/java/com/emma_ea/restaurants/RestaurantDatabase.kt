package com.emma_ea.restaurants

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Restaurant::class],
    version = 1,
    exportSchema = false
)
abstract class RestaurantDatabase : RoomDatabase() {

    abstract val dao: RestaurantsDao

    companion object {
        private fun buildDatabase(context: Context): RestaurantDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                RestaurantDatabase::class.java,
                "restaurants_database")
                .fallbackToDestructiveMigration()
                .build()

        @Volatile
        private var INSTANCE: RestaurantsDao? = null

        fun getDaoIntance(context: Context): RestaurantsDao {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = buildDatabase(context).dao
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

}