package com.mx.plantas.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    @Query("SELECT * FROM plants ORDER BY name ASC")
    fun getAll(): Flow<List<Plant>>

    @Query("SELECT * FROM plants")
    suspend fun getAllOnce(): List<Plant>

    @Query("SELECT * FROM plants WHERE id = :plantId")
    suspend fun getPlantById(plantId: Long): Plant?

    @Query(
        "SELECT * FROM plants WHERE LOWER(name) LIKE '%' || LOWER(:query) || '%' OR LOWER(scientificName) LIKE '%' || LOWER(:query) || '%' ORDER BY name ASC"
    )
    fun searchPlants(query: String): Flow<List<Plant>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(plants: List<Plant>)

    @Update
    suspend fun updatePlant(plant: Plant)

    @Query("DELETE FROM plants")
    suspend fun deleteAll()
}
