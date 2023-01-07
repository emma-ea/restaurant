package com.emma_ea.restaurants.restaurants.data.di

import android.content.Context
import androidx.room.Room
import com.emma_ea.restaurants.restaurants.data.Endpoint
import com.emma_ea.restaurants.restaurants.data.local.RestaurantDatabase
import com.emma_ea.restaurants.restaurants.data.local.RestaurantsDao
import com.emma_ea.restaurants.restaurants.data.remote.RestaurantApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RestaurantModule {

    @Singleton
    @Provides
    fun provideRoomDatabase(
        @ApplicationContext appContext: Context
    ): RestaurantDatabase {
        return Room.databaseBuilder(
            appContext,
            RestaurantDatabase::class.java,
            "restaurants_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideRoomDao(database: RestaurantDatabase): RestaurantsDao {
        return database.dao
    }

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(Endpoint.firebaseDbUrl)
            .build()
    }

    @Provides
    fun provideRetrofitApi(retrofit: Retrofit): RestaurantApiService {
        return retrofit.create(RestaurantApiService::class.java)
    }


}