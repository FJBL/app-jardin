package com.mx.plantas.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class PlantSqliteHelper(context: Context) : SQLiteOpenHelper(
    context.applicationContext,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS plants (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                scientificName TEXT NOT NULL,
                family TEXT NOT NULL,
                description TEXT NOT NULL,
                careLevel TEXT NOT NULL,
                waterNeed TEXT NOT NULL,
                sunlight TEXT NOT NULL,
                difficulty TEXT NOT NULL,
                imageResName TEXT NOT NULL,
                isFavorite INTEGER NOT NULL DEFAULT 0
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS plants")
        onCreate(db)
    }

    fun insertPlant(plant: Plant): Long {
        val values = ContentValues().apply {
            put("name", plant.name)
            put("scientificName", plant.scientificName)
            put("family", plant.family)
            put("description", plant.description)
            put("careLevel", plant.careLevel)
            put("waterNeed", plant.waterNeed)
            put("sunlight", plant.sunlight)
            put("difficulty", plant.difficulty)
            put("imageResName", plant.imageResName)
            put("isFavorite", if (plant.isFavorite) 1 else 0)
        }

        return writableDatabase.insertWithOnConflict(
            "plants",
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    fun updatePlant(plant: Plant) {
        val values = ContentValues().apply {
            put("name", plant.name)
            put("scientificName", plant.scientificName)
            put("family", plant.family)
            put("description", plant.description)
            put("careLevel", plant.careLevel)
            put("waterNeed", plant.waterNeed)
            put("sunlight", plant.sunlight)
            put("difficulty", plant.difficulty)
            put("imageResName", plant.imageResName)
            put("isFavorite", if (plant.isFavorite) 1 else 0)
        }

        writableDatabase.update(
            "plants",
            values,
            "id = ?",
            arrayOf(plant.id.toString())
        )
    }

    fun insertPlants(plants: List<Plant>) {
        writableDatabase.beginTransaction()
        try {
            plants.forEach { insertPlant(it) }
            writableDatabase.setTransactionSuccessful()
        } finally {
            writableDatabase.endTransaction()
        }
    }

    fun getAllPlants(): List<Plant> {
        val plants = mutableListOf<Plant>()
        val cursor = readableDatabase.query(
            "plants",
            null,
            null,
            null,
            null,
            null,
            "name ASC"
        )

        with(cursor) {
            while (moveToNext()) {
                plants.add(mapRowToPlant(this))
            }
            close()
        }

        return plants
    }

    fun getPlantById(plantId: Long): Plant? {
        val cursor = readableDatabase.query(
            "plants",
            null,
            "id = ?",
            arrayOf(plantId.toString()),
            null,
            null,
            null
        )

        return if (cursor.moveToFirst()) {
            val plant = mapRowToPlant(cursor)
            cursor.close()
            plant
        } else {
            cursor.close()
            null
        }
    }

    fun seedDefaultsIfEmpty(defaultPlants: List<Plant>) {
        if (getAllPlants().isNotEmpty()) return
        insertPlants(defaultPlants)
    }

    private fun mapRowToPlant(cursor: android.database.Cursor): Plant {
        return Plant(
            id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
            name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
            scientificName = cursor.getString(cursor.getColumnIndexOrThrow("scientificName")),
            family = cursor.getString(cursor.getColumnIndexOrThrow("family")),
            description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
            careLevel = cursor.getString(cursor.getColumnIndexOrThrow("careLevel")),
            waterNeed = cursor.getString(cursor.getColumnIndexOrThrow("waterNeed")),
            sunlight = cursor.getString(cursor.getColumnIndexOrThrow("sunlight")),
            difficulty = cursor.getString(cursor.getColumnIndexOrThrow("difficulty")),
            imageResName = cursor.getString(cursor.getColumnIndexOrThrow("imageResName")),
            isFavorite = cursor.getInt(cursor.getColumnIndexOrThrow("isFavorite")) == 1
        )
    }

    companion object {
        private const val DATABASE_NAME = "plants_sqlite.db"
        private const val DATABASE_VERSION = 1
    }
}
