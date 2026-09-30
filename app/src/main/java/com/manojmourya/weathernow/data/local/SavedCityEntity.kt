package com.manojmourya.weathernow.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "saved_cities",
    indices = [Index(value = ["latitude", "longitude"], unique = true)],
)
data class SavedCityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val country: String?,
    val admin1: String?,
    val latitude: Double,
    val longitude: Double,
    val isSelected: Boolean = false,
    val savedAt: Long = System.currentTimeMillis(),
)
