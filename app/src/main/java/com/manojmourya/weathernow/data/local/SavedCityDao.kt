package com.manojmourya.weathernow.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedCityDao {

    @Query("SELECT * FROM saved_cities ORDER BY savedAt DESC")
    fun getAll(): Flow<List<SavedCityEntity>>

    @Query("SELECT * FROM saved_cities WHERE isSelected = 1 LIMIT 1")
    fun getSelected(): Flow<SavedCityEntity?>

    @Query("SELECT EXISTS(SELECT 1 FROM saved_cities WHERE latitude = :latitude AND longitude = :longitude)")
    fun exists(latitude: Double, longitude: Double): Flow<Boolean>

    @Query("SELECT * FROM saved_cities WHERE latitude = :latitude AND longitude = :longitude LIMIT 1")
    suspend fun findByCoordinates(latitude: Double, longitude: Double): SavedCityEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(city: SavedCityEntity): Long

    @Delete
    suspend fun delete(city: SavedCityEntity)

    @Query("UPDATE saved_cities SET isSelected = 0")
    suspend fun clearSelection()

    @Query("UPDATE saved_cities SET isSelected = 1 WHERE id = :id")
    suspend fun setSelected(id: Long)

    @Query("SELECT COUNT(*) FROM saved_cities")
    suspend fun count(): Int
}
