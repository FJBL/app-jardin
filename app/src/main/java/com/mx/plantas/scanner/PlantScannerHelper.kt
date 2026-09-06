package com.mx.plantas.scanner

import com.mx.plantas.data.Plant
import java.text.Normalizer

object PlantScannerHelper {
    fun findBestMatch(plants: List<Plant>, scanText: String): Plant? {
        val normalizedInput = normalize(scanText)
        if (normalizedInput.isBlank()) return null

        return plants
            .map { plant ->
                val searchText = listOf(
                    plant.name,
                    plant.scientificName,
                    plant.family,
                    plant.description
                ).joinToString(" ")
                val score = scoreMatch(normalizedInput, normalize(searchText), plant)
                plant to score
            }
            .filter { it.second > 0 }
            .maxByOrNull { it.second }
            ?.first
    }

    private fun scoreMatch(normalizedInput: String, searchableText: String, plant: Plant): Int {
        val inputTokens = normalizedInput
            .split(Regex("\\s+"))
            .filter { it.length > 2 }
            .filterNot { it in setOf("plant", "planta", "la", "de", "y", "con") }

        if (inputTokens.isEmpty()) return 0

        var score = 0

        if (searchableText.contains(normalizedInput)) score += 100

        val candidateFields = listOf(
            normalize(plant.name),
            normalize(plant.scientificName),
            normalize(plant.family)
        )

        for (field in candidateFields) {
            if (field == normalizedInput) score += 80
            if (field.contains(normalizedInput)) score += 65
        }

        for (token in inputTokens) {
            if (searchableText.contains(token)) score += 15
            if (candidateFields.any { it.contains(token) }) score += 25
        }

        return score
    }

    private fun normalize(value: String): String {
        val stripped = Normalizer.normalize(value, Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}".toRegex(), "")
            .lowercase()
            .replace("[\\n\\r\\t]".toRegex(), " ")
            .replace("[^a-z0-9 ]".toRegex(), " ")
            .replace("\\s+".toRegex(), " ")
            .trim()
        return stripped
    }
}
