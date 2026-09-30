package com.manojmourya.weathernow.domain.repository

import com.manojmourya.weathernow.domain.model.City
import com.manojmourya.weathernow.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface CityRepository {

    /** Searches cities by name via the geocoding API. */
    fun searchCities(query: String): Flow<Resource<List<City>>>

    /** Locally persisted favorite cities, most recently saved first. */
    fun getSavedCities(): Flow<List<City>>

    /** The city currently selected to drive the Home screen, if any. */
    fun getSelectedCity(): Flow<City?>

    /** Persists [city] as a favorite. If it is the first saved city, it becomes selected. */
    suspend fun saveCity(city: City)

    /** Removes [city] from favorites. */
    suspend fun deleteCity(city: City)

    /** Marks [city] as the selected Home-screen city, saving it first if needed. */
    suspend fun selectCity(city: City)

    /** True if a city with the same coordinates is already saved. */
    fun isCitySaved(latitude: Double, longitude: Double): Flow<Boolean>
}
