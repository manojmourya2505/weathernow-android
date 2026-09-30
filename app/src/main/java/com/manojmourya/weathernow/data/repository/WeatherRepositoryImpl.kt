package com.manojmourya.weathernow.data.repository

import com.manojmourya.weathernow.data.mapper.toDomain
import com.manojmourya.weathernow.data.remote.service.ForecastApiService
import com.manojmourya.weathernow.domain.model.WeatherForecast
import com.manojmourya.weathernow.domain.repository.WeatherRepository
import com.manojmourya.weathernow.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val forecastApiService: ForecastApiService,
) : WeatherRepository {

    override fun getWeatherForecast(latitude: Double, longitude: Double): Flow<Resource<WeatherForecast>> = flow {
        emit(Resource.Loading)
        try {
            val response = forecastApiService.getForecast(latitude = latitude, longitude = longitude)
            emit(Resource.Success(response.toDomain()))
        } catch (e: IOException) {
            emit(Resource.Error("No internet connection. Please check your network and try again."))
        } catch (e: HttpException) {
            emit(Resource.Error("Server error (${e.code()}). Please try again later."))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Something went wrong. Please try again."))
        }
    }
}
