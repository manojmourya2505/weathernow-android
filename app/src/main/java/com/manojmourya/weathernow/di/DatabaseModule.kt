package com.manojmourya.weathernow.di

import android.content.Context
import androidx.room.Room
import com.manojmourya.weathernow.data.local.SavedCityDao
import com.manojmourya.weathernow.data.local.WeatherNowDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DATABASE_NAME = "weathernow.db"

    @Provides
    @Singleton
    fun provideWeatherNowDatabase(@ApplicationContext context: Context): WeatherNowDatabase =
        Room.databaseBuilder(context, WeatherNowDatabase::class.java, DATABASE_NAME).build()

    @Provides
    @Singleton
    fun provideSavedCityDao(database: WeatherNowDatabase): SavedCityDao = database.savedCityDao()
}
