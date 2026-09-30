package com.manojmourya.weathernow.data.mapper

import com.manojmourya.weathernow.data.local.SavedCityEntity
import com.manojmourya.weathernow.domain.model.City

fun SavedCityEntity.toDomain(): City = City(
    id = id,
    name = name,
    country = country,
    admin1 = admin1,
    latitude = latitude,
    longitude = longitude,
    isSelected = isSelected,
)

fun City.toEntity(isSelected: Boolean = this.isSelected): SavedCityEntity = SavedCityEntity(
    id = id ?: 0,
    name = name,
    country = country,
    admin1 = admin1,
    latitude = latitude,
    longitude = longitude,
    isSelected = isSelected,
)
