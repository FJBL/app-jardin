# 🌿 AppPlantas2 - Resumen Completo del Proyecto

## 📱 ¿QUÉ HACE LA APLICACIÓN?

**AppPlantas2** es una aplicación Android diseñada para **identificar, gestionar y aprender sobre plantas**. Permite a los usuarios:

1. ✅ **Escanear plantas por foto** → Identifica la especie automáticamente
2. ✅ **Ver catálogo de plantas** → Lista completa con detalles
3. ✅ **Ver información detallada** → Cuidados, riego, luz, dificultad
4. ✅ **Buscar plantas** → Por nombre común o científico
5. ✅ **Marcar favoritas** → Guardar plantas de interés
6. ✅ **Agregar plantas nuevas** → Crear tu propio catálogo personalizado
7. ✅ **Editar información** → Modificar detalles de plantas

---

## 🎯 FUNCIONALIDAD PRINCIPAL: ESCANEO DE PLANTAS

### Cómo Funciona:

```
1. Usuario abre app → Pantalla principal (Lista de Plantas)
2. Click en botón "📷 Escanear Planta"
3. Se abre la cámara → Toma foto de una planta
4. Google ML Kit Image Labeling analiza la imagen
5. Identifica características visuales (forma, color, textura)
6. Busca coincidencia en base de datos local
7. Muestra planta identificada con información completa
```

### Algoritmo de Identificación:

```kotlin
Para cada etiqueta detectada por ML Kit:
  Para cada planta en base de datos:
    → Compara nombre planta vs etiqueta
    → Compara nombre científico vs etiqueta
    → Compara familia botánica vs etiqueta
    → Calcula score de confianza
    
Resultado: Planta con mejor score
```

---

## 📊 ESTRUCTURA DEL PROYECTO

### Capas de la Arquitectura:

#### 1. **Data Layer** (Capa de Datos)
```
com.mx.plantas.data/
├── Plant.kt                  ← Modelo de datos (Room Entity)
├── PlantDao.kt              ← Interface de acceso a BD (Room)
├── PlantDatabase.kt         ← Base de datos Room (SQLite)
├── PlantRepository.kt       ← Abstracción de datos
└── PlantSqliteHelper.kt     ← Manejo SQLite alternativo
```

**Responsabilidades:**
- Almacenar plantas en SQLite (local, sin internet)
- Acceder a datos de forma segura
- Buscar plantas por nombre/familia
- Cargar y guardar datos

#### 2. **Scanner/ML Layer** (Clasificación Visual)
```
com.mx.plantas.scanner/
├── PlantImageClassifier.kt  ← Identifica plantas por imagen
└── PlantScannerHelper.kt    ← Matching de texto (legacy)
```

**PlantImageClassifier.kt:**
- Usa Google ML Kit Image Labeling
- Analiza imágenes automáticamente
- Compara etiquetas con plantas de BD
- Genera puntuación de confianza

#### 3. **UI Layer** (Interfaz de Usuario)
```
com.mx.plantas.ui/
├── PlantViewModel.kt        ← Lógica de negocio
├── PlantListFragment.kt     ← Pantalla principal (lista)
├── PlantDetailFragment.kt   ← Detalles de planta
├── AddPlantFragment.kt      ← Agregar nueva planta
├── EditPlantFragment.kt     ← Editar planta
└── PlantAdapter.kt          ← Adaptador para RecyclerView
```

---

## 💾 MODELO DE DATOS

### Estructura de una Planta (Plant.kt):

```kotlin
data class Plant(
    val id: Long                    // ID único en BD
    val name: String                // "Aloe Vera"
    val scientificName: String      // "Aloe barbadensis miller"
    val family: String              // "Asphodelaceae"
    val description: String         // Descripción detallada
    val careLevel: String           // "Fácil" / "Moderada" / "Difícil"
    val waterNeed: String           // "Baja" / "Media" / "Alta"
    val sunlight: String            // "Sol directo" / "Luz indirecta"
    val difficulty: String          // Nivel de dificultad
    val imageResName: String        // Ruta de imagen
    val isFavorite: Boolean         // ¿Es favorita?
)
```

### Plantas de Ejemplo (Pre-cargadas):

1. **Aloe Vera** - Suculenta, muy fácil
2. **Monstera deliciosa** - Follaje grande, moderada
3. **Helecho de Boston** - Verde intenso, fácil
4. **Suculenta Jade** - Decorativa, fácil
5. **Ficus lyrata** - Árbol interior, moderada
6. **Pothos** - Crecimiento rápido, muy fácil

---

## 🔄 FLUJO DE NAVEGACIÓN

```
MainActivity (Contenedor)
│
└── Navigation Graph
    ├── PlantListFragment (Pantalla Principal)
    │   ├── [📷 Escanear] → Abre cámara → PlantImageClassifier
    │   ├── [🔍 Buscar] → Filtra lista dinámicamente
    │   ├── [Click planta] → va a PlantDetailFragment
    │   └── [➕ Agregar] → va a AddPlantFragment
    │
    ├── PlantDetailFragment (Ver Detalles)
    │   ├── [✏️ Editar] → va a EditPlantFragment
    │   ├── [⭐ Favorita] → Marca/desmarca
    │   └── [◄ Atrás] → vuelve a PlantListFragment
    │
    ├── AddPlantFragment (Agregar Nueva Planta)
    │   ├── [Capturar foto] → Abre cámara
    │   ├── [Llenar datos] → Nombre, familia, etc.
    │   ├── [💾 Guardar] → Agrega a BD
    │   └── [◄ Cancelar] → Vuelve
    │
    └── EditPlantFragment (Editar Planta)
        ├── [Cargar datos] → De BD
        ├── [Modificar] → Campos editables
        ├── [💾 Guardar] → Actualiza BD
        └── [◄ Cancelar] → Descarta cambios
```

---

## 🧠 COMPONENTES CLAVE

### 1. **PlantViewModel.kt**
- Centro neurálgico de la lógica
- Gestiona estado de plantas
- Ejecuta búsquedas
- Coordina con ImageClassifier
- Proporciona datos a la UI

```kotlin
// Métodos principales:
val filteredPlants           // Lista filtrada reactiva
fun findPlantByImage()       // Identifica por foto
fun onSearchChanged()        // Filtra plantas
fun addPlant()               // Agrega nueva planta
fun updatePlant()            // Modifica planta
```

### 2. **PlantImageClassifier.kt**
- Interfaz con Google ML Kit
- Clasifica imágenes automáticamente
- Compara resultados con BD

```kotlin
// Métodos principales:
fun findBestPlantMatch()     // Identifica planta en imagen
fun classifyImage()          // Obtiene etiquetas
fun calculateMatchScore()    // Computa coincidencia
```

### 3. **PlantRepository.kt**
- Patrón Repository (abstracción)
- Acceso a BD Room
- Acceso a BD SQLite alternativa

```kotlin
val allPlants               // Flow de todas las plantas
fun searchPlants()          // Busca por query
suspend fun addPlant()      // Agrega
suspend fun updatePlant()   // Modifica
```

### 4. **PlantListFragment.kt**
- Pantalla principal
- Lista de plantas con RecyclerView
- Campo de búsqueda
- Botón escanear
- Maneja resultado de foto

```kotlin
// Funcionalidad:
- Mostrar lista completa
- Filtrar por búsqueda
- Manejar clic en planta
- Abrir cámara para escanear
- Procesar imagen identificada
```

---

## 🎨 INTERFAZ DE USUARIO

### Pantalla Principal (PlantListFragment)
```
┌─────────────────────────────────┐
│ 🔧 PLANTAS                      │
├─────────────────────────────────┤
│ 🔍 [Buscar plantas...]         │
├─────────────────────────────────┤
│ ┌───────────────────────────┐   │
│ │ Aloe Vera                 │   │
│ │ Aloe barbadensis miller   │ ◄─┼─ Click → Ver detalles
│ │ Asphodelaceae - Fácil     │   │
│ └───────────────────────────┘   │
│ ┌───────────────────────────┐   │
│ │ Monstera deliciosa        │   │
│ │ Monstera deliciosa        │   │
│ │ Araceae - Moderada        │   │
│ └───────────────────────────┘   │
│                                 │
│              [📷 Escanear]      │
└─────────────────────────────────┘
```

### Pantalla de Detalles (PlantDetailFragment)
```
┌─────────────────────────────────┐
│ ◄ Aloe Vera                     │
├─────────────────────────────────┤
│ [Imagen de planta]              │
│                                 │
│ Nombre Científico:              │
│ Aloe barbadensis miller         │
│                                 │
│ Familia: Asphodelaceae          │
│ Nivel de Cuidado: Fácil         │
│ Riego: Baja necesidad           │
│ Luz: Sol indirecto              │
│ Dificultad: Baja                │
│                                 │
│ Descripción:                    │
│ Planta suculenta ideal...       │
│                                 │
│ [✏️ Editar] [⭐ Favorita]       │
└─────────────────────────────────┘
```

---

## 🔐 BASE DE DATOS

### Estructura SQLite:

```sql
CREATE TABLE plants (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT,
    scientificName TEXT,
    family TEXT,
    description TEXT,
    careLevel TEXT,
    waterNeed TEXT,
    sunlight TEXT,
    difficulty TEXT,
    imageResName TEXT,
    isFavorite INTEGER
)
```

### Acceso:
- **Room ORM** - Acceso tipado y seguro
- **PlantDao** - Operaciones CRUD
- **PlantDatabase** - Singleton
- **SQLiteHelper** - Respaldo alternativo

---

## 📚 STACK TECNOLÓGICO

### Android Framework:
- **Language**: Kotlin 2.2.10
- **API Mínima**: Android 7.0 (API 24)
- **API Destino**: Android 15 (API 36)
- **JVM Target**: Java 11

### Librerías principales:
```gradle
// UI & Navigation
androidx.appcompat:appcompat
androidx.fragment:fragment-ktx
androidx.navigation:navigation-fragment-ktx
androidx.navigation:navigation-ui-ktx
androidx.constraintlayout:constraintlayout

// Data & Database
androidx.room:room-runtime
androidx.room:room-ktx
androidx.room:room-compiler (KSP)

// State Management
androidx.lifecycle:lifecycle-runtime-ktx
androidx.lifecycle:lifecycle-viewmodel-ktx
androidx.lifecycle:lifecycle-livedata-ktx

// ML Kit
com.google.mlkit:image-labeling (Clasificación visual)

// UI Components
androidx.recyclerview:recyclerview
com.google.android.material:material
```

---

## 🎯 CARACTERÍSTICAS IMPLEMENTADAS

### ✅ Completas:
- [x] Escaneo de plantas por foto
- [x] Identificación visual con ML Kit
- [x] Catálogo de plantas
- [x] Vista detallada
- [x] Búsqueda y filtrado
- [x] Marcar favoritas
- [x] Base de datos local SQLite
- [x] Arquitectura MVVM
- [x] Navegación con Navigation Graph

### 🔄 En Desarrollo:
- [ ] Exportar/importar plantas
- [ ] Historial de escaneos
- [ ] Compartir plantas
- [ ] Widget de homescreen
- [ ] Recordatorios de riego

### 🚀 Futuro:
- [ ] API PlantNet (online)
- [ ] Modelo TensorFlow Lite personalizado
- [ ] AR (Realidad Aumentada)
- [ ] Comunidad/compartir

---

## 🚀 CÓMO USAR LA APP

### 1. Escanear Planta:
```
1. Abre app
2. Click en "📷 Escanear Planta"
3. Toma foto clara de la planta
4. App identifica automáticamente
5. Click en "Ver Detalles" si deseas
```

### 2. Buscar Planta:
```
1. En pantalla principal
2. Escribe en campo "🔍 Buscar plantas..."
3. Lista se filtra en tiempo real
4. Click en planta para ver detalles
```

### 3. Agregar Planta Personalizada:
```
1. Click en "➕ Agregar"
2. Llenar datos (nombre, familia, etc.)
3. Capturar foto (opcional)
4. Click "💾 Guardar"
```

### 4. Editar Planta:
```
1. Ir a detalles de planta
2. Click "✏️ Editar"
3. Modificar datos
4. Click "💾 Guardar"
```

---

## 📋 ESTADO DEL PROYECTO

### ✅ Compilación:
```
✓ Build exitoso
✓ Sin errores de compilación
✓ Todas las dependencias resueltas
✓ AndroidManifest.xml configurado
```

### ✅ Arquitectura:
```
✓ Patrón MVVM implementado
✓ Repository pattern para datos
✓ Reactive flows con Kotlin Flow
✓ View Binding configurado
✓ Navigation Component integrado
```

### ✅ Funcionalidades:
```
✓ Cámara integrada
✓ Google ML Kit Image Labeling
✓ Room database
✓ Búsqueda y filtrado
✓ Edición de datos
```

---

## 🎓 CONCEPTOS CLAVE

1. **Google ML Kit Image Labeling**: 
   - Análisis de imágenes en dispositivo
   - No requiere internet
   - Automático y rápido

2. **MVVM Architecture**:
   - ViewModel: Lógica
   - Repository: Datos
   - Fragment: UI
   - LiveData/Flow: Reactividad

3. **Room Database**:
   - SQLite tipado
   - DAO pattern
   - Coroutines support

4. **Navigation Component**:
   - Single Activity Architecture
   - Fragmentos como pantallas
   - Gestión de back stack

---

## 📞 MANTENIMIENTO

### Para agregar nueva planta:
1. Editar `PlantRepository.samplePlants()`
2. Agregar objeto `Plant()`
3. Recompilar

### Para personalizar ML Kit:
1. Ajustar threshold en `PlantImageClassifier.kt`
2. Modificar lógica de matching
3. Recompilar

### Para agregar modelo TensorFlow:
1. Descargar modelo `.tflite`
2. Colocar en `app/src/main/assets/`
3. Actualizar `PlantImageClassifier.kt`

---

## 🎉 CONCLUSIÓN

**AppPlantas2** es una aplicación completa, moderna y funcional para la identificación de plantas. 

Utiliza:
- ✅ Tecnología AI (Google ML Kit)
- ✅ Base de datos local
- ✅ Arquitectura clean (MVVM)
- ✅ Best practices Android

**¡Listo para usar y personalizar!** 🌿
