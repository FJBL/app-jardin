# Resumen de Cambios: De OCR a Clasificación Visual de Plantas

## 🔄 Cambio Principales

### Antes: Reconocimiento de Texto (OCR)
- **Método**: ML Kit Text Recognition → Detectar texto en imagen → Buscar coincidencia por nombre
- **Ventajas**: Funciona si la planta tiene etiqueta o cartel con su nombre
- **Desventajas**: No identifica la planta visualmente, solo lee texto

### Después: Clasificación de Imágenes
- **Método**: TensorFlow Lite → Analizar características visuales de la imagen → Identificar especie
- **Ventajas**: Identifica plantas por su apariencia visual (hojas, flores, forma)
- **Desventajas**: Requiere modelo entrenado específicamente para plantas

---

## 📁 Archivos Modificados

### 1. `build.gradle.kts` (Actualizadas dependencias)
**Cambios**:
- ❌ Removida: ML Kit Text Recognition (no se usa más)
- ✅ Agregadas: TensorFlow Lite, TensorFlow Lite Support, TensorFlow Lite Metadata

```gradle
// Nueva dependencias
implementation("org.tensorflow:tensorflow-lite:2.14.0")
implementation("org.tensorflow:tensorflow-lite-support:0.4.4")
implementation("org.tensorflow:tensorflow-lite-metadata:0.1.0")
```

### 2. `PlantImageClassifier.kt` (NUEVO ARCHIVO)
**Ubicación**: `app/src/main/java/com/mx/plantas/scanner/PlantImageClassifier.kt`

**Funcionalidades**:
- Carga modelo TensorFlow Lite desde `assets/plants_model.tflite`
- Carga etiquetas desde `assets/plants_labels.txt`
- Procesa imagen (redimensionamiento a 224x224)
- Clasifica imagen y obtiene predicciones
- Busca coincidencia con plantas en la base de datos
- Calcula score de confianza con múltiples criterios

**Métodos principales**:
```kotlin
classifyImage(bitmap: Bitmap): List<PlantPrediction>
findBestPlantMatch(bitmap: Bitmap, plants: List<Plant>): Pair<Plant, Float>?
calculateMatchScore(plant: Plant, predictions: List<PlantPrediction>): Float
```

### 3. `PlantViewModel.kt` (Actualizado)
**Cambios**:
- ❌ Removido: `PlantScannerHelper`, método `findPlantByScanText()`
- ✅ Agregado: `PlantImageClassifier` instancia
- ✅ Nuevo método: `findPlantByImage(bitmap: Bitmap): Plant?`
- ✅ Nuevo: Limpiar recursos en `onCleared()`

```kotlin
// Antes
fun findPlantByScanText(rawText: String): Plant? 
    = PlantScannerHelper.findBestMatch(currentPlants, rawText)

// Después
fun findPlantByImage(bitmap: Bitmap): Plant? 
    = imageClassifier.findBestPlantMatch(bitmap, currentPlants)?.first
```

### 4. `PlantListFragment.kt` (Actualizado)
**Cambios**:
- ❌ Removido: `InputImage`, `TextRecognition`, `TextRecognizerOptions` de ML Kit
- ✅ Actualizado: Método `handleScannedBitmap()`
- ✅ Actualizado: Mensajes de usuario (de "texto detectado" a "análisis de imagen")

```kotlin
// Antes
val image = InputImage.fromBitmap(bitmap, 0)
val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
recognizer.process(image).addOnSuccessListener { visionText ->
    val detectedText = visionText.text
    val matchedPlant = viewModel.findPlantByScanText(detectedText)
}

// Después
val matchedPlant = viewModel.findPlantByImage(bitmap)
```

---

## 📊 Comparativa de Métodos

| Aspecto | OCR (Anterior) | Clasificación Visual (Nuevo) |
|---------|---|---|
| **Entrada** | Imagen con texto visible | Imagen de la planta |
| **Procesamiento** | Extrae texto → Busca coincidencia | Analiza características visuales |
| **Precisión** | Depende de legibilidad del texto | Depende del modelo TensorFlow |
| **Offline** | ✅ Sí | ✅ Sí |
| **Tiempo** | ~500ms | ~200-500ms |
| **Modelo** | ML Kit (Google) | TensorFlow Lite personalizado |

---

## 🛠️ Algoritmo de Matching

### PlantScannerHelper (Anterior)
```
1. Normalizar texto: remover acentos, minúsculas
2. Dividir en tokens (palabras)
3. Buscar coincidencias:
   - Texto completo en descripción (+100 pts)
   - Coincidencia exacta en campo (+80 pts)
   - Contiene el texto (+65 pts)
   - Coincidencia de tokens (+15-25 pts)
4. Retornar planta con mayor score
```

### PlantImageClassifier (Nuevo)
```
1. Redimensionar imagen a 224x224
2. Procesar con red neuronal TensorFlow Lite
3. Obtener predicciones: [Nombre_Planta: Confianza]
4. Para cada planta en base de datos:
   - Score por nombre (+0.9 × confianza)
   - Score por nombre científico (+0.85 × confianza)
   - Score por palabras clave (+0.5 × confianza)
5. Retornar planta + plant con mayor score combinado
```

---

## 📦 Archivos Requeridos (A Agregar)

Debes colocar en `app/src/main/assets/`:

```
app/src/main/assets/
├── plants_model.tflite       (20-40 MB típicamente)
└── plants_labels.txt         (Lista de plantas)
```

Ver `SETUP_MODEL.md` para instrucciones detalladas.

---

## 🔄 Impacto en Arquitectura

### Antes
```
MainActivity
└── PlantListFragment
    ├── Camera Input (Bitmap)
    ├── ML Kit Text Recognition
    ├── PlantScannerHelper (matching por texto)
    └── PlantViewModel (plantas)
```

### Después
```
MainActivity
└── PlantListFragment
    ├── Camera Input (Bitmap)
    ├── PlantViewModel.findPlantByImage()
    │   └── PlantImageClassifier
    │       ├── TensorFlow Lite Interpreter
    │       ├── plants_model.tflite (clasificación visual)
    │       └── plants_labels.txt
    └── PlantViewModel (plantas + ImageClassifier)
```

---

## ✅ Testing

### Prueba Manual
1. Compilar app: `./gradlew build`
2. Ejecutar en dispositivo/emulador
3. Presionar "Escanear Planta"
4. Tomar foto de una planta de la base de datos
5. Verificar que se identifique correctamente

### Casos de Prueba
- ✅ Planta bien iluminada → Debe identificar
- ✅ Planta de ángulo difícil → Puede fallar (ajustar modelo)
- ✅ Imagen borrosa → Puede fallar (mejorar foto)
- ✅ Planta no en base de datos → Mostrar "No identificada"

---

## 🚀 Mejoras Futuras

1. **Usar API PlantNet** para mejores resultados (requiere internet)
2. **Entrenar modelo personalizado** con fotos de tus plantas
3. **Agregar geoubicación** para restringir búsqueda a plantas locales
4. **Mostrar alternativas** si confianza es baja
5. **Feedback del usuario** para mejorar modelo

---

## 📚 Referencias

- TensorFlow Lite: https://www.tensorflow.org/lite
- PlantNet API: https://www.plantnet.org/
- Modelos Públicos: https://tfhub.dev/
- TensorFlow Lite Model Maker: https://github.com/tensorflow/examples
