package com.mx.plantas.data

import android.content.Context
import kotlinx.coroutines.flow.Flow

class PlantRepository(
    private val plantDao: PlantDao,
    context: Context
) {
    private val sqliteHelper = PlantSqliteHelper(context.applicationContext)

    val allPlants: Flow<List<Plant>> = plantDao.getAll()

    fun searchPlants(query: String): Flow<List<Plant>> = plantDao.searchPlants(query)

    suspend fun getPlantById(plantId: Long): Plant? = plantDao.getPlantById(plantId)

    suspend fun getAllPlants(): List<Plant> = plantDao.getAllOnce()

    fun getAllSqlitePlants(): List<Plant> = sqliteHelper.getAllPlants()

    suspend fun addPlant(plant: Plant) {
        plantDao.insertAll(listOf(plant))
        sqliteHelper.insertPlant(plant)
    }

    suspend fun updatePlant(plant: Plant) {
        plantDao.updatePlant(plant)
        sqliteHelper.updatePlant(plant)
    }

    fun addPlantToSqlite(plant: Plant): Long = sqliteHelper.insertPlant(plant)

    suspend fun seedDatabase() {
        val existingPlants = plantDao.getAllOnce()
        if (existingPlants.size < samplePlants().size) {
            val defaultPlants = samplePlants()
            plantDao.insertAll(defaultPlants)
            sqliteHelper.seedDefaultsIfEmpty(defaultPlants)
        }
    }

    private fun samplePlants(): List<Plant> = listOf(
        Plant(
            name = "Aloe Vera",
            scientificName = "Aloe barbadensis miller",
            family = "Asphodelaceae",
            description = "Planta suculenta ideal para principiantes, famosa por sus propiedades hidratantes y resistencia a la sequía.",
            careLevel = "Fácil",
            waterNeed = "Baja",
            sunlight = "Sol indirecto",
            difficulty = "Baja",
            imageResName = "suculenta",
            isFavorite = true
        ),
        Plant(
            name = "Monstera deliciosa",
            scientificName = "Monstera deliciosa",
            family = "Araceae",
            description = "De follaje grande y perforado, se adapta bien a interiores con luz brillante y humedad moderada.",
            careLevel = "Moderada",
            waterNeed = "Media",
            sunlight = "Luz brillante",
            difficulty = "Media",
            imageResName = "monstera"
        ),
        Plant(
            name = "Helecho de Boston",
            scientificName = "Nephrolepis exaltata",
            family = "Nephrolepidaceae",
            description = "Hojas verdes y suaves que aportan un ambiente húmedo y fresco a cualquier rincón de la casa.",
            careLevel = "Fácil",
            waterNeed = "Alta",
            sunlight = "Semisombra",
            difficulty = "Baja",
            imageResName = "images"
        ),
        Plant(
            name = "Suculenta Jade",
            scientificName = "Crassula ovata",
            family = "Crassulaceae",
            description = "Suculenta ornamental con hojas redondeadas, muy decorativa y resistente a periodos de sequía.",
            careLevel = "Fácil",
            waterNeed = "Baja",
            sunlight = "Sol directo",
            difficulty = "Baja",
            imageResName = "suculenta_jade"
        ),
        Plant(
            name = "Ficus lyrata",
            scientificName = "Ficus lyrata",
            family = "Moraceae",
            description = "Árbol de interior de hojas grandes y de aspecto elegante, ideal para salas con mucha luz.",
            careLevel = "Moderada",
            waterNeed = "Media",
            sunlight = "Luz indirecta",
            difficulty = "Media",
            imageResName = "tulipanes"
        ),
        Plant(
            name = "Pothos",
            scientificName = "Epipremnum aureum",
            family = "Araceae",
            description = "Planta de crecimiento rápido y muy adaptable, excelente para principiantes y espacios pequeños.",
            careLevel = "Fácil",
            waterNeed = "Media",
            sunlight = "Luz indirecta",
            difficulty = "Baja",
            imageResName = "filodendro",
            isFavorite = true
        ),
        Plant(
            name = "Sansevieria",
            scientificName = "Sansevieria trifasciata",
            family = "Asparagaceae",
            description = "Conocida como lengua de suegra, es extremadamente resistente y tolera condiciones de poca luz. Excelente para purificar el aire.",
            careLevel = "Muy Fácil",
            waterNeed = "Baja",
            sunlight = "Baja a media",
            difficulty = "Muy Baja",
            imageResName = "sansevieria",
            isFavorite = true
        ),
        Plant(
            name = "Cactus",
            scientificName = "Ferocactus latispinus",
            family = "Cactaceae",
            description = "Cactus ornamental robusto, ideal para lugares secos y soleados. Requiere mínimos cuidados de riego.",
            careLevel = "Muy Fácil",
            waterNeed = "Muy Baja",
            sunlight = "Sol directo",
            difficulty = "Muy Baja",
            imageResName = "cactus"
        ),
        Plant(
            name = "Lirio de Paz",
            scientificName = "Spathiphyllum wallisii",
            family = "Araceae",
            description = "Planta elegante con flores blancas, mejora la calidad del aire y florece en condiciones de poca luz.",
            careLevel = "Fácil",
            waterNeed = "Media",
            sunlight = "Semisombra",
            difficulty = "Baja",
            imageResName = "lirio_paz",
            isFavorite = true
        ),
        Plant(
            name = "Begonia",
            scientificName = "Begonia rex",
            family = "Begoniaceae",
            description = "Planta ornamental con hojas coloridas y patrones atractivos. Requiere humedad moderada.",
            careLevel = "Moderada",
            waterNeed = "Media",
            sunlight = "Luz indirecta",
            difficulty = "Media",
            imageResName = "begonia"
        ),
        Plant(
            name = "Violeta Africana",
            scientificName = "Saintpaulia ionantha",
            family = "Gesneriaceae",
            description = "Pequeña planta con flores vistosas, ideal para ventanales. Prefiere suelo húmedo pero no encharcado.",
            careLevel = "Moderada",
            waterNeed = "Media",
            sunlight = "Luz brillante indirecta",
            difficulty = "Media",
            imageResName = "violeta_africana"
        ),
        Plant(
            name = "Peperomia",
            scientificName = "Peperomia obtusifolia",
            family = "Piperaceae",
            description = "Planta compacta y resistente con hojas ornamentales. Perfecta para escritorios y espacios pequeños.",
            careLevel = "Fácil",
            waterNeed = "Media",
            sunlight = "Luz indirecta",
            difficulty = "Baja",
            imageResName = "peperomia"
        ),
        Plant(
            name = "Palmera Areca",
            scientificName = "Dypsis lutescens",
            family = "Arecaceae",
            description = "Palmera tropical de interior elegante que aporta un toque exótico. Necesita humedad y espacio.",
            careLevel = "Moderada",
            waterNeed = "Media",
            sunlight = "Luz brillante",
            difficulty = "Media",
            imageResName = "palmera_areca"
        ),
        Plant(
            name = "Caladio",
            scientificName = "Caladium bicolor",
            family = "Araceae",
            description = "Planta tropical con hojas llamativas en colores rojo, rosa y blanco. Requiere condiciones cálidas y húmedas.",
            careLevel = "Moderada",
            waterNeed = "Alta",
            sunlight = "Luz indirecta",
            difficulty = "Media",
            imageResName = "caladio"
        ),
        Plant(
            name = "Dieffenbachia",
            scientificName = "Dieffenbachia seguine",
            family = "Araceae",
            description = "Planta tropical decorativa con hojas variegadas. Tolera variaciones de luz, ideal para oficinas.",
            careLevel = "Fácil",
            waterNeed = "Media",
            sunlight = "Luz indirecta",
            difficulty = "Baja",
            imageResName = "dieffenbachia"
        ),
        Plant(
            name = "Anthurio",
            scientificName = "Anthurium andraeanum",
            family = "Araceae",
            description = "Planta tropical con flores en forma de corazón, disponible en varios colores. Prefiere ambiente húmedo.",
            careLevel = "Moderada",
            waterNeed = "Media",
            sunlight = "Luz brillante indirecta",
            difficulty = "Media",
            imageResName = "anthurio",
            isFavorite = true
        ),
        Plant(
            name = "Dracaena",
            scientificName = "Dracaena fragrans",
            family = "Asparagaceae",
            description = "Planta de interior robusta con follaje verde oscuro. Tolera condiciones de poca luz muy bien.",
            careLevel = "Muy Fácil",
            waterNeed = "Baja",
            sunlight = "Baja a media",
            difficulty = "Muy Baja",
            imageResName = "dracaena"
        ),
        Plant(
            name = "Filodendro",
            scientificName = "Philodendron hederaceum",
            family = "Araceae",
            description = "Planta trepadora versátil con hojas en forma de corazón. Muy resistente y de fácil propagación.",
            careLevel = "Fácil",
            waterNeed = "Media",
            sunlight = "Luz indirecta",
            difficulty = "Baja",
            imageResName = "filodendro"
        ),
        Plant(
            name = "Oxalis",
            scientificName = "Oxalis triangularis",
            family = "Oxalidaceae",
            description = "Planta pequeña con hojas púrpura triangulares y pequeñas flores rosadas. Muy decorativa.",
            careLevel = "Fácil",
            waterNeed = "Media",
            sunlight = "Luz indirecta",
            difficulty = "Baja",
            imageResName = "oxalis"
        ),
        Plant(
            name = "Stromanthe",
            scientificName = "Stromanthe sanguinea",
            family = "Marantaceae",
            description = "Planta tropical con hojas variegadas en rojo y verde. Muy ornamental, requiere humedad.",
            careLevel = "Moderada",
            waterNeed = "Media",
            sunlight = "Luz indirecta",
            difficulty = "Media",
            imageResName = "stromanthe"
        )
    )
}
