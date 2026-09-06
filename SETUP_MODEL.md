# Configuración del Modelo de Clasificación de Plantas

## 📋 Descripción

La aplicación ahora utiliza **TensorFlow Lite** para identificar plantas directamente desde imágenes fotográficas en lugar de usar OCR (reconocimiento de texto).

## ⚙️ Instalación del Modelo

### Opción 1: Usar un Modelo Pre-entrenado (Recomendado)

#### 1.1 Descargar Modelo Público de Plantas

Hay varios modelos disponibles públicamente:

**A) PlantNet (Abierto y Gratuito)**
- Sitio: https://www.plantnet.org/
- Descargar modelo TFLite desde TensorFlow Hub

**B) TensorFlow Hub - Plant Identification**
- Link: https://tfhub.dev/
- Buscar "plant classification" o "flowers classification"

**C) MobileNet v2 Fine-tuned para Plantas**
- Opción ligera y rápida para dispositivos móviles
- Disponible en TensorFlow Hub

#### 1.2 Preparar los Archivos

Una vez descargado el modelo, necesitas:

1. **Archivo del modelo**: `plants_model.tflite` (típicamente 20-40 MB)
2. **Archivo de etiquetas**: `plants_labels.txt` (lista de nombres de plantas)

### Opción 2: Entrenar tu Propio Modelo

Si deseas entrenar un modelo personalizado con tus plantas específicas:

```bash
# Usar TensorFlow Lite Model Maker
pip install tflite-model-maker

# Documentación: https://github.com/tensorflow/examples/tree/master/tensorflow_lite/python/train
```

## 📂 Estructura de Directorios

```
app/
├── src/
│   └── main/
│       └── assets/
│           ├── plants_model.tflite      ← Modelo TensorFlow Lite
│           └── plants_labels.txt        ← Etiquetas de plantas
```

### Crear el Directorio `assets`

```bash
mkdir -p app/src/main/assets
```

## 📝 Formato de `plants_labels.txt`

El archivo debe contener una etiqueta por línea:

```
Aloe Vera
Monstera Deliciosa
Helecho de Boston
Suculenta Jade
Ficus Lyrata
Pothos
Cactus Segaro
Rosa
Lavanda
Tulipán
```

**Nota**: Asegúrate de que los nombres de plantas en el archivo de etiquetas coincidan (parcialmente) con los nombres en tu base de datos.

## 🔧 Modificar la Configuración

Si tu modelo tiene diferentes dimensiones de entrada, actualiza `PlantImageClassifier.kt`:

```kotlin
// Cambiar estos valores según tu modelo
ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR)  // Ajusta 224, 224 si es necesario
```

### Verificar Dimensiones del Modelo

Para saber qué dimensiones necesita tu modelo:

```bash
# Usar TensorFlow Lite interpreter
python3 -c "
import tensorflow as tf
interpreter = tf.lite.Interpreter(model_path='plants_model.tflite')
interpreter.allocate_tensors()
input_details = interpreter.get_input_details()
print(f'Input Shape: {input_details[0][\"shape\"]}')
"
```

## 🚀 Compilar y Probar

Una vez agregados los archivos:

```bash
# Limpiar y compilar
./gradlew clean build

# O en Android Studio:
# Build > Clean Project
# Build > Rebuild Project
```

## 📊 Ajuste Fino del Modelo

Si el modelo no identifica bien algunas plantas, puedes:

1. **Usar más datos de entrenamiento** en tu modelo personalizado
2. **Ajustar el umbral de confianza** en `PlantImageClassifier.kt`:

```kotlin
.filter { it.confidence > 0.1f }  // Cambiar 0.1f a un valor diferente
```

3. **Mejorar el matching score** en la función `calculateMatchScore()`

## 🔍 Troubleshooting

### Error: "Model file not found"
- Verifica que `plants_model.tflite` esté en `app/src/main/assets/`
- Asegúrate de que el archivo no esté corrupto

### Error: "Labels file not found"
- Verifica que `plants_labels.txt` esté en `app/src/main/assets/`
- Revisa que no haya espacios extra o caracteres especiales

### Identificación Incorrecta
- Aumenta la cantidad y variedad de plantas en `plants_labels.txt`
- Considera usar un modelo con mayor precisión (más grande)
- Ajusta el umbral de confianza

## 📚 Recursos Adicionales

- **TensorFlow Lite Guide**: https://www.tensorflow.org/lite/guide
- **Plant Species Classification**: https://github.com/tensorflow/examples/tree/master/tensorflow_lite
- **PlantNet Dataset**: https://www.plantnet.org/ (Millones de fotos de plantas)

## ✅ Verificación

Para verificar que todo está funcionando:

1. Abre la app
2. Presiona el botón "Escanear Planta"
3. Toma una foto de una planta
4. La app debería identificarla y mostrar la información

Si aparece el mensaje "No se identificó la planta", verifica que:
- Los archivos de modelo y etiquetas existen
- El nombre de la planta en la foto coincide con tu base de datos
- El modelo es lo suficientemente preciso
