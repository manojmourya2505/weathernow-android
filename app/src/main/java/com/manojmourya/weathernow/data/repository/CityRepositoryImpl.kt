package com.manojmourya.weathernow.data.repository

import androidx.room.withTransaction
import com.manojmourya.weathernow.data.local.SavedCityDao
import com.manojmourya.weathernow.data.local.WeatherNowDatabase
import com.manojmourya.weathernow.data.mapper.toDomain
import com.manojmourya.weathernow.data.mapper.toEntity
import com.manojmourya.weathernow.data.remote.service.GeocodingApiService
import com.manojmourya.weathernow.domain.model.City
import com.manojmourya.weathernow.domain.repository.CityRepository
import com.manojmourya.weathernow.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class CityRepositoryImpl @Inject constructor(
    private val geocodingApiService: GeocodingApiService,
    private val savedCityDao: SavedCityDao,
    private val database: WeatherNowDatabase,
) : CityRepository {

    override fun searchCities(query: String): Flow<Resource<List<City>>> = flow {
        emit(Resource.Loading)
        if (query.isBlank()) {
            emit(Resource.Success(emptyList()))
            return@flow
        }
        try {
            val response = geocodingApiService.searchCities(name = query.trim())
            val results = response.results?.map { it.toDomain() } ?: emptyList()
            emit(Resource.Success(results))
        } catch (e: IOException) {
            emit(Resource.Error("No internet connection. Please check your network and try again."))
        } catch (e: HttpException) {
            emit(Resource.Error("Server error (${e.code()}). Please try again later."))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Something went wrong. Please try again."))
        }
    }

    override fun getSavedCities(): Flow<List<City>> =
        savedCityDao.getAll().map { entities -> entities.map { it.toDomain() } }

    override fun getSelectedCity(): Flow<City?> =
        savedCityDao.getSelected().map { it?.toDomain() }

    override fun isCitySaved(latitude: Double, longitude: Double): Flow<Boolean> =
        savedCityDao.exists(latitude, longitude)

    override suspend fun saveCity(city: City) {
        database.withTransaction {
            val existing = savedCityDao.findByCoordinates(city.latitude, city.longitude)
            if (existing != null) return@withTransaction
            val shouldAutoSelect = savedCityDao.count() == 0
            savedCityDao.insert(city.toEntity(isSelected = shouldAutoSelect))
        }
    }

    override suspend fun deleteCity(city: City) {
        val id = city.id ?: return
        savedCityDao.delete(city.toEntity(isSelected = false).copy(id = id))
    }

    override suspend fun selectCity(city: City) {
        database.withTransaction {
            val existing = city.id?.let { existingId -> existingId }
                ?: savedCityDao.findByCoordinates(city.latitude, city.longitude)?.id

            val targetId = existing ?: savedCityDao.insert(city.toEntity(isSelected = false))

            savedCityDao.clearSelection()
            savedCityDao.setSelected(targetId)
        }
    }
}
