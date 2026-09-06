package com.mx.plantas.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plants")
data class Plant(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val scientificName: String,
    val family: String,
    val description: String,
    val careLevel: String,
    val waterNeed: String,
    val sunlight: String,
    val difficulty: String,
    val imageResName: String = "ic_plant_placeholder",
    val isFavorite: Boolean = false
)
