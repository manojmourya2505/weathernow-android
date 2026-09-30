package com.manojmourya.weathernow.data.remote.service

import com.manojmourya.weathernow.data.remote.dto.GeocodingResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

/** https://geocoding-api.open-meteo.com/v1/search */
interface GeocodingApiService {
    @GET("v1/search")
    suspend fun searchCities(
        @Query("name") name: String,
        @Query("count") count: Int = 20,
        @Query("language") language: String = "en",
        @Query("format") format: String = "json",
    ): GeocodingResponseDto
}
