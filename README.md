# 📚 Índice de Documentación - AppPlantas2

## 🎯 Comienza por Aquí

Dependiendo de lo que quieras hacer, comienza por:

### 1. **Quiero entender qué hace la app**
   👉 **[RESUMEN_VISUAL.md](RESUMEN_VISUAL.md)** - Diagramas y flujos visuales
   
   - Qué es AppPlantas2
   - Características principales
   - Pantallas y navegación
   - Flujo de escaneo
   - Estructura de datos

### 2. **Quiero ejecutar la aplicación**
   👉 **[COMO_EJECUTAR.md](COMO_EJECUTAR.md)** - Guía paso a paso

   - Abrir emulador/dispositivo
   - Compilar y ejecutar
   - Solucionar problemas comunes
   - Checklist de verificación

### 3. **Quiero detalles técnicos completos**
   👉 **[RESUMEN_PROYECTO_COMPLETO.md](RESUMEN_PROYECTO_COMPLETO.md)** - Guía exhaustiva

   - Componentes clave
   - Arquitectura MVVM
   - Base de datos
   - Stack tecnológico
   - Mantenimiento

### 4. **Cambios recientes a la app**
   👉 **[CAMBIOS_OCR_A_VISION.md](CAMBIOS_OCR_A_VISION.md)** - Qué cambió

   - De OCR a Visión
   - Archivos modificados
   - Nuevo componente PlantImageClassifier
   - Impacto arquitectónico

### 5. **Configuración avanzada (Modelos TensorFlow)**
   👉 **[SETUP_MODEL.md](SETUP_MODEL.md)** - Integración de modelos
   
   - Descargar modelos pre-entrenados
   - Usar PlantNet API
   - Crear modelos personalizados
   - Troubleshooting

### 6. **Inicio rápido con modelos**
   👉 **[QUICK_START_MODEL.md](QUICK_START_MODEL.md)** - Guía rápida
   
   - Opción más rápida
   - Combinar métodos
   - Comparativas de modelos

---

## 📁 Estructura de Archivos

```
AppPlantas2/
├── 📚 DOCUMENTACIÓN
│   ├── RESUMEN_VISUAL.md                    ← Diagramas visuales
│   ├── RESUMEN_PROYECTO_COMPLETO.md         ← Guía técnica
│   ├── COMO_EJECUTAR.md                     ← Instrucciones ejecución
│   ├── CAMBIOS_OCR_A_VISION.md              ← Cambios recientes
│   ├── SETUP_MODEL.md                       ← Config avanzada
│   ├── QUICK_START_MODEL.md                 ← Inicio rápido
│   ├── RESUMEN_IMPLEMENTACION.md            ← Resumen cambios
│   └── README.md                            ← Este archivo
│
├── 🔧 CONFIGURACIÓN
│   ├── build.gradle.kts                     ← Dependencias
│   ├── settings.gradle.kts                  ← Módulos
│   └── app/
│       ├── build.gradle.kts                 ← App específico
│       └── src/
│           ├── main/
│           │   ├── AndroidManifest.xml      ← Permisos
│           │   ├── java/com/mx/plantas/     ← Código fuente
│           │   ├── res/                     ← Recursos
│           │   └── assets/                  ← Etiquetas de plantas
│           ├── androidTest/                 ← Tests
│           └── test/
│
├── 📜 SCRIPTS
│   ├── run_app.bat                          ← Script Windows
│   └── run_app.sh                           ← Script Unix
│
└── 📋 VERSIÓN & GIT
    ├── .gitignore                           ← Archivos ignorados
    └── local.properties                     ← Config local

```

---

## 🗂️ Componentes Principales

### Capa de Datos (Data Layer)
```
com.mx.plantas.data/
├── Plant.kt                  ← Modelo de datos
├── PlantDao.kt              ← Acceso a BD
├── PlantDatabase.kt         ← Base de datos Room
├── PlantRepository.kt       ← Abstracción
└── PlantSqliteHelper.kt     ← SQLite alternativo
```

### Capa de ML (Machine Learning)
```
com.mx.plantas.scanner/
├── PlantImageClassifier.kt  ← Google ML Kit (NUEVO)
└── PlantScannerHelper.kt    ← Legacy (OCR)
```

### Capa de UI (User Interface)
```
com.mx.plantas.ui/
├── PlantViewModel.kt        ← Lógica
├── PlantListFragment.kt     ← Pantalla principal
├── PlantDetailFragment.kt   ← Detalles
├── AddPlantFragment.kt      ← Agregar
├── EditPlantFragment.kt     ← Editar
└── PlantAdapter.kt          ← Adaptador lista
```

### Actividad Principal
```
com.mx.plantas/
├── MainActivity.kt          ← Contenedor
├── FirstFragment.kt         ← Legacy
└── SecondFragment.kt        ← Legacy
```

---

## ⚡ Comandos Útiles

### Compilar
```bash
cd c:\Users\root\Documents\AppPlantas2
.\gradlew.bat build
```

### Ejecutar
```bash
# Automático (recomendado)
.\run_app.bat

# Manual - Opción 1: Desde Android Studio
# Click ▶️ Run (o Shift + F10)

# Manual - Opción 2: Línea de comandos
.\gradlew.bat installDebug
adb shell am start -n com.mx.plantas/.MainActivity
```

### Limpiar
```bash
.\gradlew.bat clean
```

### Ver tareas disponibles
```bash
.\gradlew.bat tasks
```

---

## 🎯 Tareas Comunes

### Agregar nueva planta al catálogo
1. Abrir `PlantRepository.kt`
2. En función `samplePlants()`
3. Agregar objeto `Plant()`
4. Recompilar

### Cambiar umbral de confianza
1. Abrir `PlantImageClassifier.kt`
2. Modificar `CONFIDENCE_THRESHOLD = 0.3f`
3. Recompilar

### Agregar nuevo fragmento/pantalla
1. Crear archivo `*Fragment.kt`
2. Agregar al Navigation Graph XML
3. Implementar navegación

### Modificar modelo de datos
1. Editar `Plant.kt`
2. Crear Migration en Room
3. Actualizar `PlantDao.kt`

---

## 📊 Quick Reference - Arquitectura

```
ENTRADA                      PROCESAMIENTO              SALIDA
═════════════════════════════════════════════════════════════════

Foto (Bitmap)
    ↓
PlantImageClassifier (ML Kit)
    ↓
Etiquetas + Confianza
    ↓
PlantViewModel (Búsqueda)
    ↓
PlantRepository (Acceso BD)
    ↓
PlantDatabase (Room/SQLite)
    ↓
Lista de Plantas
    ↓
UI (PlantListFragment)
    ↓
Pantalla con Plantas
```

---

## 🔐 Seguridad & Privacidad

✅ **Base de datos local** - No se envía información a servidores
✅ **Offline first** - Funciona sin conexión a internet
✅ **Sin credenciales** - No requiere login/contraseña
✅ **Datos privados** - Todo se almacena en el dispositivo

---

## 🚀 Próximos Pasos Sugeridos

### Corto Plazo (1-2 semanas)
- [ ] Probar escaneo de plantas
- [ ] Agregar más plantas al catálogo
- [ ] Personalizar información botánica
- [ ] Revisar UX/UI

### Mediano Plazo (1-2 meses)
- [ ] Entrenar modelo personalizado con TensorFlow
- [ ] Integrar PlantNet API (online fallback)
- [ ] Historial de escaneos
- [ ] Recordatorios de riego

### Largo Plazo (3+ meses)
- [ ] AR (Realidad Aumentada) para overlay
- [ ] Comunidad/compartir plantas
- [ ] Sincronización en la nube
- [ ] Widget de homescreen
- [ ] App widget con predicciones

---

## 📞 FAQ

### P: ¿Necesito conexión a internet?
R: No. La app funciona 100% offline con Google ML Kit.

### P: ¿Puedo agregar mis propias plantas?
R: Sí. Click en "Agregar Planta" en la pantalla principal.

### P: ¿Cómo mejoro la identificación?
R: Toma fotos más claras o entrena un modelo personalizado (ver SETUP_MODEL.md).

### P: ¿Dónde se guardan los datos?
R: En SQLite local, dentro de la app. Se sincroniza automáticamente.

### P: ¿Puedo exportar mis plantas?
R: Por ahora no, pero es una característica futura planificada.

---

## 📖 Referencias Externas

- **Android Developers**: https://developer.android.com
- **Google ML Kit**: https://developers.google.com/ml-kit
- **Room Database**: https://developer.android.com/training/data-storage/room
- **Kotlin Coroutines**: https://kotlinlang.org/docs/coroutines-overview.html
- **PlantNet API**: https://www.plantnet.org

---

## ✨ Créditos & Licencia

**AppPlantas2** - Aplicación de Identificación de Plantas

- Desarrollado con Kotlin y Android
- Usa Google ML Kit para IA
- Arquitectura MVVM con Repository Pattern
- Material Design para UI

---

## 📝 Notas de Desarrollo

### Última Compilación
```
✓ Fecha: Septiembre 2026
✓ Estado: EXITOSA
✓ Errores: 0
✓ Warnings: 0
```

### Cambios Recientes
- Migración de OCR a Clasificación Visual
- Integración de Google ML Kit Image Labeling
- Nuevo componente PlantImageClassifier
- Actualización de UI/UX

### Próxima Revisión
- Pruebas de escaneo extensivas
- Optimización de rendimiento
- Feedback de usuarios

---

**¡Gracias por usar AppPlantas2!** 🌿✨
