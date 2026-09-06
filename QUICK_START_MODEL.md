# Instalación Rápida: Descargando Modelo Pre-entrenado

## 🎯 Opción Más Rápida (Recomendada)

### Usar Modelo de Google TensorFlow Hub

#### Paso 1: Descargar Modelo

Descarga un modelo pre-entrenado de flores/plantas desde:
```
https://tfhub.dev/google/tf2-preview/mobilenet_v2/classification/2
```

O para plantas específicamente:
```
https://www.kaggle.com/models/google/mobilenetv2/frameworks/TensorFlowLite
```

#### Paso 2: Convertir a TFLite

```python
import tensorflow_hub as hub
import tensorflow as tf

# Descargar modelo
model = hub.load('https://tfhub.dev/google/tf2-preview/mobilenet_v2/classification/2')

# Convertir a TFLite
converter = tf.lite.TFLiteConverter.from_saved_model('path/to/model')
tflite_model = converter.convert()

# Guardar
with open('plants_model.tflite', 'wb') as f:
    f.write(tflite_model)
```

#### Paso 3: Crear Archivo de Etiquetas

Crea `plants_labels.txt` con las plantas de tu base de datos:

```
Aloe Vera
Monstera deliciosa
Helecho de Boston
Suculenta Jade
Ficus lyrata
Pothos
```

---

## 📱 Alternativa: Usar PlantNet API (Online)

Si prefieres no incluir modelo localmente:

```kotlin
// En PlantImageClassifier.kt, reemplazar clasificación local con llamada HTTP a PlantNet
suspend fun classifyImageOnline(bitmap: Bitmap): List<PlantPrediction> {
    val file = convertBitmapToFile(bitmap)
    val requestBody = MultipartBody.Part.createFormData("images", "plant.jpg", file.asRequestBody())
    
    return retrofit.create(PlantNetService::class.java)
        .identifyPlant(requestBody, "en")
}
```

**Ventajas**: No necesitas modelo local, siempre actualizado
**Desventajas**: Requiere internet, más lento

---

## 📂 Estructura Correcta

```
AppPlantas2/
└── app/
    └── src/
        └── main/
            ├── AndroidManifest.xml
            ├── java/
            │   └── com/mx/plantas/
            │       ├── scanner/
            │       │   └── PlantImageClassifier.kt ✅
            │       └── ...
            └── assets/ ← CREAR ESTA CARPETA
                ├── plants_model.tflite
                └── plants_labels.txt
```

---

## ⚡ Test Rápido

Después de agregar los archivos, verifica en Android Studio:

1. Sync Gradle (Android Studio → File → Sync Now)
2. Ejecuta: `./gradlew build`
3. Si sin errores, los assets están correctamente colocados ✅

---

## 🔧 Si hay Error en Tiempo de Ejecución

**Error**: "Model file not found"

**Solución**:
```kotlin
// Verificar archivos en assets
fun debugAssets(context: Context) {
    val assetManager = context.assets
    val files = assetManager.list("") ?: emptyArray()
    Log.d("Assets", files.joinToString())
}
```

Debería mostrar:
```
[plants_model.tflite, plants_labels.txt, ...]
```

---

## 💡 Tip: Combinar Ambos Métodos

Para máxima precisión, puedes usar:

```kotlin
// 1. Primero intentar clasificación local rápida
val localResult = classifyImage(bitmap)

// 2. Si confianza es baja, consultar PlantNet (si conectado)
if (localResult.first().confidence < 0.5f && isNetworkAvailable()) {
    return classifyImageOnline(bitmap)
}

return localResult
```

---

## 📊 Tamaños Típicos de Modelos

| Modelo | Tamaño | Precisión | Velocidad |
|--------|--------|-----------|-----------|
| MobileNet v2 (flores) | 14 MB | 80-85% | ⚡⚡⚡ |
| ResNet50 | 100 MB | 90-95% | ⚡⚡ |
| EfficientNet | 30-50 MB | 85-90% | ⚡⚡ |

**Recomendación**: Usar MobileNet v2 para balance de velocidad y precisión.
