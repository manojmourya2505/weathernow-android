package com.manojmourya.weathernow.di

import com.manojmourya.weathernow.data.repository.CityRepositoryImpl
import com.manojmourya.weathernow.data.repository.WeatherRepositoryImpl
import com.manojmourya.weathernow.domain.repository.CityRepository
import com.manojmourya.weathernow.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(impl: WeatherRepositoryImpl): WeatherRepository

    @Binds
    @Singleton
    abstract fun bindCityRepository(impl: CityRepositoryImpl): CityRepository
}
