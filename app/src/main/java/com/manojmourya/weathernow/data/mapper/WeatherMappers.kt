package com.manojmourya.weathernow.data.mapper

import com.manojmourya.weathernow.data.remote.dto.ForecastResponseDto
import com.manojmourya.weathernow.data.remote.dto.GeocodingResultDto
import com.manojmourya.weathernow.domain.model.City
import com.manojmourya.weathernow.domain.model.CurrentWeather
import com.manojmourya.weathernow.domain.model.DailyForecastItem
import com.manojmourya.weathernow.domain.model.HourlyForecastItem
import com.manojmourya.weathernow.domain.model.WeatherForecast

fun GeocodingResultDto.toDomain(): City = City(
    id = null,
    name = name,
    country = country,
    admin1 = admin1,
    latitude = latitude,
    longitude = longitude,
)

/**
 * Maps the raw forecast response to the domain model, trimming the hourly
 * list down to the 24 entries from (and including) the current hour onward,
 * which is what the Home screen's hourly strip displays.
 */
fun ForecastResponseDto.toDomain(): WeatherForecast {
    val currentDto = this.current
    val currentWeather = CurrentWeather(
        temperature = currentDto.temperature2m,
        feelsLike = currentDto.apparentTemperature,
        humidity = currentDto.relativeHumidity2m,
        windSpeed = currentDto.windSpeed10m,
        weatherCode = currentDto.weatherCode,
        time = currentDto.time,
    )

    val currentHourIndex = hourly.time.indexOfFirst { it >= currentDto.time }
        .let { if (it == -1) 0 else it }

    val hourlyItems = hourly.time.indices
        .drop(currentHourIndex)
        .take(24)
        .map { i ->
            HourlyForecastItem(
                time = hourly.time[i],
                temperature = hourly.temperature2m[i],
                weatherCode = hourly.weatherCode[i],
            )
        }

    val dailyItems = daily.time.indices.map { i ->
        DailyForecastItem(
            date = daily.time[i],
            weatherCode = daily.weatherCode[i],
            maxTemperature = daily.temperature2mMax[i],
            minTemperature = daily.temperature2mMin[i],
            precipitationProbability = daily.precipitationProbabilityMax[i],
        )
    }

    return WeatherForecast(
        timezone = timezone,
        current = currentWeather,
        hourly = hourlyItems,
        daily = dailyItems,
    )
}
