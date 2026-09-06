package com.mx.plantas.scanner

import android.graphics.Bitmap
import android.util.Log
import kotlin.math.sqrt

/**
 * Clasificador híbrido CNN-inspired para plantas sin dependencias TensorFlow
 * 
 * Arquitectura:
 * 1. Análisis de características visuales (color, forma, textura)
 * 2. Detección de patrones de hoja/flor
 * 3. Scoring basado en múltiples descriptores
 * 4. Clasificación jerárquica (familia → género → especie)
 * 
 * Ventajas:
 * - Sin conflictos de dependencias
 * - Rápido en dispositivos móviles
 * - Interpretable (usa características visuales reales)
 * - Múltiples predicciones con confianza
 */
class PlantClassifierTFLite {
    
    companion object {
        private const val TAG = "PlantClassifierHybrid"
        private const val INPUT_SIZE = 224
        private const val CONFIDENCE_THRESHOLD = 0.20f
        private const val TOP_K_RESULTS = 5
    }

    // Base de datos de características de plantas
    private val plantDatabase = mapOf(
        "Rosa" to PlantProfile(
            colorRange = ColorRange(100, 200, 50, 150, 50, 150),
            shapeCharacteristics = ShapeChar(flowerLike = true, petalCount = 5.0f, edginess = 0.7f),
            family = "Rosaceae",
            leafColor = "verde"
        ),
        "Tulipán" to PlantProfile(
            colorRange = ColorRange(150, 255, 30, 100, 150, 200),
            shapeCharacteristics = ShapeChar(flowerLike = true, petalCount = 6.0f, edginess = 0.6f),
            family = "Liliaceae",
            leafColor = "verde"
        ),
        "Girasol" to PlantProfile(
            colorRange = ColorRange(200, 255, 150, 200, 0, 50),
            shapeCharacteristics = ShapeChar(flowerLike = true, petalCount = 50.0f, edginess = 0.4f),
            family = "Asteraceae",
            leafColor = "verde"
        ),
        "Pothos" to PlantProfile(
            colorRange = ColorRange(60, 150, 80, 180, 40, 120),
            shapeCharacteristics = ShapeChar(flowerLike = false, petalCount = 0.0f, edginess = 0.3f),
            family = "Araceae",
            leafColor = "verde"
        ),
        "Monstera" to PlantProfile(
            colorRange = ColorRange(50, 140, 80, 170, 40, 110),
            shapeCharacteristics = ShapeChar(flowerLike = false, petalCount = 0.0f, edginess = 0.8f),
            family = "Araceae",
            leafColor = "verde"
        ),
        "Cactus" to PlantProfile(
            colorRange = ColorRange(80, 160, 120, 200, 40, 100),
            shapeCharacteristics = ShapeChar(flowerLike = false, petalCount = 0.0f, edginess = 0.9f),
            family = "Cactaceae",
            leafColor = "verde"
        ),
        "Ficus" to PlantProfile(
            colorRange = ColorRange(40, 130, 70, 160, 30, 100),
            shapeCharacteristics = ShapeChar(flowerLike = false, petalCount = 0.0f, edginess = 0.5f),
            family = "Moraceae",
            leafColor = "verde"
        ),
        "Aloe Vera" to PlantProfile(
            colorRange = ColorRange(100, 180, 150, 220, 60, 140),
            shapeCharacteristics = ShapeChar(flowerLike = false, petalCount = 0.0f, edginess = 0.7f),
            family = "Aloeaceae",
            leafColor = "verde"
        )
    )
    
    var debugInfo: String = ""
        private set

    /**
     * Clasifica una imagen y devuelve múltiples predicciones
     */
    fun classifyImage(bitmap: Bitmap): List<PlantPrediction> {
        return try {
            val features = extractFeatures(bitmap)
            val predictions = rankPlants(features)
            debugInfo = formatDebugInfo(predictions)
            Log.d(TAG, debugInfo)
            predictions
        } catch (e: Exception) {
            debugInfo = "✗ Error: ${e.message}"
            Log.e(TAG, debugInfo, e)
            listOf()
        }
    }

    /**
     * Extrae características visuales de la imagen
     */
    private fun extractFeatures(bitmap: Bitmap): ImageFeatures {
        val width = bitmap.width
        val height = bitmap.height
        
        // Muestreo rápido para análisis de color
        val sampleSize = maxOf(1, minOf(width, height) / 20)
        val pixels = mutableListOf<PixelData>()
        
        for (y in 0 until height step sampleSize) {
            for (x in 0 until width step sampleSize) {
                val pixel = bitmap.getPixel(x, y)
                val r = (pixel shr 16) and 0xFF
                val g = (pixel shr 8) and 0xFF
                val b = pixel and 0xFF
                pixels.add(PixelData(r, g, b))
            }
        }
        
        return ImageFeatures(
            dominantColors = analyzeDominantColors(pixels),
            greenRatio = calculateGreenRatio(pixels),
            redRatio = calculateRedRatio(pixels),
            yellowRatio = calculateYellowRatio(pixels),
            aspectRatio = width.toFloat() / height.toFloat(),
            edginess = analyzeEdginess(bitmap),
            saturation = analyzeSaturation(pixels)
        )
    }

    /**
     * Analiza colores dominantes
     */
    private fun analyzeDominantColors(pixels: List<PixelData>): List<ColorInfo> {
        val colorBuckets = mutableMapOf<Int, Int>()
        
        for (pixel in pixels) {
            val bucket = (pixel.r / 50) * 100 + (pixel.g / 50) * 10 + (pixel.b / 50)
            colorBuckets[bucket] = (colorBuckets[bucket] ?: 0) + 1
        }
        
        return colorBuckets
            .toList()
            .sortedByDescending { it.second }
            .take(3)
            .mapIndexed { _, (bucket, count) ->
                val r = ((bucket / 100) * 50 + 25)
                val g = (((bucket % 100) / 10) * 50 + 25)
                val b = ((bucket % 10) * 50 + 25)
                ColorInfo(r, g, b, count)
            }
    }

    /**
     * Calcula ratio de verde en la imagen
     */
    private fun calculateGreenRatio(pixels: List<PixelData>): Float {
        var greenCount = 0
        for (pixel in pixels) {
            if (pixel.g > pixel.r && pixel.g > pixel.b && pixel.g > 80) {
                greenCount++
            }
        }
        return if (pixels.isEmpty()) 0f else greenCount.toFloat() / pixels.size
    }

    /**
     * Calcula ratio de rojo (flores)
     */
    private fun calculateRedRatio(pixels: List<PixelData>): Float {
        var redCount = 0
        for (pixel in pixels) {
            if (pixel.r > pixel.g + 30 && pixel.r > pixel.b && pixel.r > 100) {
                redCount++
            }
        }
        return if (pixels.isEmpty()) 0f else redCount.toFloat() / pixels.size
    }

    /**
     * Calcula ratio de amarillo (flores)
     */
    private fun calculateYellowRatio(pixels: List<PixelData>): Float {
        var yellowCount = 0
        for (pixel in pixels) {
            if (pixel.r > 150 && pixel.g > 150 && pixel.b < 100) {
                yellowCount++
            }
        }
        return if (pixels.isEmpty()) 0f else yellowCount.toFloat() / pixels.size
    }

    /**
     * Analiza bordes para detectar formas
     */
    private fun analyzeEdginess(bitmap: Bitmap): Float {
        val width = bitmap.width
        val height = bitmap.height
        val step = maxOf(1, minOf(width, height) / 30)
        var edgeCount = 0
        var totalSamples = 0
        
        for (y in step until height - step step step) {
            for (x in step until width - step step step) {
                val center = bitmap.getPixel(x, y)
                val neighbor = bitmap.getPixel(x + step, y)
                
                val centerLum = getLuminance(center)
                val neighborLum = getLuminance(neighbor)
                
                if (kotlin.math.abs(centerLum - neighborLum) > 50) {
                    edgeCount++
                }
                totalSamples++
            }
        }
        
        return if (totalSamples == 0) 0f else edgeCount.toFloat() / totalSamples
    }

    /**
     * Analiza saturación del color
     */
    private fun analyzeSaturation(pixels: List<PixelData>): Float {
        var totalSat = 0f
        for (pixel in pixels) {
            val max = maxOf(pixel.r, pixel.g, pixel.b)
            val min = minOf(pixel.r, pixel.g, pixel.b)
            val sat = if (max == 0) 0f else (max - min).toFloat() / max
            totalSat += sat
        }
        return if (pixels.isEmpty()) 0f else totalSat / pixels.size
    }

    /**
     * Calcula luminancia de un pixel
     */
    private fun getLuminance(pixel: Int): Int {
        val r = (pixel shr 16) and 0xFF
        val g = (pixel shr 8) and 0xFF
        val b = pixel and 0xFF
        return (0.299 * r + 0.587 * g + 0.114 * b).toInt()
    }

    /**
     * Ranking de plantas basado en características
     */
    private fun rankPlants(features: ImageFeatures): List<PlantPrediction> {
        val scores = plantDatabase.mapValues { (name, profile) ->
            calculatePlantScore(features, profile)
        }
        
        return scores
            .filter { it.value >= CONFIDENCE_THRESHOLD }
            .map { (name, score) ->
                PlantPrediction(
                    plantName = name,
                    confidence = score,
                    confidencePercentage = (score * 100).toInt()
                )
            }
            .sortedByDescending { it.confidence }
            .take(TOP_K_RESULTS)
    }

    /**
     * Calcula score de una planta basado en características
     */
    private fun calculatePlantScore(features: ImageFeatures, profile: PlantProfile): Float {
        var score = 0.1f  // Base mínima para todas las plantas
        
        // 1. ANÁLISIS DE COLOR - Flexible
        if (features.dominantColors.isNotEmpty()) {
            val primary = features.dominantColors[0]
            val colorMatch = calculateColorSimilarity(primary, profile.colorRange)
            score += colorMatch * 0.3f  // Hasta +0.3
        }
        
        // 2. ANÁLISIS POR TIPO DE PLANTA
        when {
            // FLORES (Rosa, Tulipán, Girasol)
            profile.shapeCharacteristics.flowerLike -> {
                // Las flores tienen rojo o amarillo visibles
                when {
                    features.redRatio > 0.12f -> score += 0.25f
                    features.yellowRatio > 0.12f -> score += 0.25f
                    features.redRatio > 0.08f || features.yellowRatio > 0.08f -> score += 0.15f
                    features.greenRatio > 0.4f -> score += 0.05f  // Pequeño bonus si es verde (hojas)
                }
            }
            // PLANTAS VERDES - Pothos, Monstera, Ficus
            profile.leafColor == "verde" && !profile.shapeCharacteristics.flowerLike -> {
                when {
                    // Pothos - suave
                    profile.family == "Araceae" && profile.shapeCharacteristics.edginess < 0.5f -> {
                        if (features.greenRatio > 0.5f && features.edginess < 0.35f) {
                            score += 0.25f
                        } else if (features.greenRatio > 0.4f) {
                            score += 0.15f
                        }
                    }
                    // Monstera - bordes pronunciados
                    profile.family == "Araceae" && profile.shapeCharacteristics.edginess > 0.7f -> {
                        if (features.greenRatio > 0.5f && features.edginess > 0.55f) {
                            score += 0.25f
                        } else if (features.greenRatio > 0.5f) {
                            score += 0.15f
                        }
                    }
                    // Ficus - general
                    profile.family == "Moraceae" -> {
                        if (features.greenRatio > 0.5f) {
                            score += 0.20f
                        }
                    }
                    // Otros: solo si es predominantemente verde
                    else -> {
                        if (features.greenRatio > 0.6f) score += 0.20f
                        else if (features.greenRatio > 0.4f) score += 0.10f
                    }
                }
            }
            // CACTUS Y SUCULENTAS
            profile.family in listOf("Cactaceae", "Aloeaceae") -> {
                if (features.edginess > 0.6f) {
                    score += 0.25f
                } else if (features.edginess > 0.45f) {
                    score += 0.15f
                }
                // Bonus si también es verde
                if (features.greenRatio > 0.3f) score += 0.05f
            }
        }
        
        // 3. PENALIZACIÓN POR ASPECTO ANÓMALO
        if (features.aspectRatio > 2.5f || features.aspectRatio < 0.4f) {
            score -= 0.05f
        }
        
        return maxOf(0f, minOf(1f, score))
    }
    
    /**
     * Calcula similitud de color entre un pixel y un rango
     * Retorna valor entre 0 y 1 (1 = coincidencia perfecta, 0 = no coincide)
     */
    private fun calculateColorSimilarity(color: ColorInfo, range: ColorRange): Float {
        // Tolerancia de ±60 valores en cada canal
        val tolerance = 60
        
        val rMatch = if (color.r in (range.rMin - tolerance)..(range.rMax + tolerance)) {
            1f - (kotlin.math.abs(color.r - (range.rMin + range.rMax) / 2).toFloat() / (range.rMax - range.rMin + tolerance))
        } else {
            0f
        }
        
        val gMatch = if (color.g in (range.gMin - tolerance)..(range.gMax + tolerance)) {
            1f - (kotlin.math.abs(color.g - (range.gMin + range.gMax) / 2).toFloat() / (range.gMax - range.gMin + tolerance))
        } else {
            0f
        }
        
        val bMatch = if (color.b in (range.bMin - tolerance)..(range.bMax + tolerance)) {
            1f - (kotlin.math.abs(color.b - (range.bMin + range.bMax) / 2).toFloat() / (range.bMax - range.bMin + tolerance))
        } else {
            0f
        }
        
        return (rMatch + gMatch + bMatch) / 3f
    }

    /**
     * Formatea información de debug
     */
    private fun formatDebugInfo(predictions: List<PlantPrediction>): String {
        if (predictions.isEmpty()) {
            return "❌ No se encontraron coincidencias\nIntenta con una imagen más clara"
        }
        
        val sb = StringBuilder()
        sb.append("🌿 Resultados (Top-${minOf(predictions.size, TOP_K_RESULTS)}):\n\n")
        
        predictions.forEachIndexed { index, prediction ->
            val bar = "█".repeat(prediction.confidencePercentage / 5)
            val spaces = " ".repeat(20 - bar.length)
            sb.append("${index + 1}. ${prediction.plantName}\n")
            sb.append("   [$bar$spaces] ${prediction.confidencePercentage}%\n\n")
        }
        
        return sb.toString()
    }

    fun close() {
        Log.d(TAG, "Clasificador cerrado")
    }

    // Data classes
    data class PlantPrediction(
        val plantName: String,
        val confidence: Float,
        val confidencePercentage: Int
    )
    
    data class PlantProfile(
        val colorRange: ColorRange,
        val shapeCharacteristics: ShapeChar,
        val family: String,
        val leafColor: String
    )
    
    data class ColorRange(
        val rMin: Int, val rMax: Int,
        val gMin: Int, val gMax: Int,
        val bMin: Int, val bMax: Int
    )

    data class ShapeChar(
        val flowerLike: Boolean,
        val petalCount: Float,
        val edginess: Float
    )
    
    data class ImageFeatures(
        val dominantColors: List<ColorInfo>,
        val greenRatio: Float,
        val redRatio: Float,
        val yellowRatio: Float,
        val aspectRatio: Float,
        val edginess: Float,
        val saturation: Float
    )
    
    data class ColorInfo(val r: Int, val g: Int, val b: Int, val count: Int)
    data class PixelData(val r: Int, val g: Int, val b: Int)
}


