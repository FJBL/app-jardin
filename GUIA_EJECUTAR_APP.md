# 🌿 GUÍA COMPLETA: Ejecutar AppPlantas2

## 🎯 ¿Por qué no corre la app?

Las razones más comunes son:

1. **❌ No hay emulador/dispositivo abierto**
2. **❌ No has presionado "Run"**
3. **❌ Errores de compilación**
4. **❌ Aplicación no está instalada**

---

## ✅ OPCIÓN 1: Ejecutar desde Android Studio (Recomendado)

### Paso 1: Abrir Android Studio
- Abre **Android Studio**
- Asegúrate de estar en el proyecto "AppPlantas2"

### Paso 2: Abrir el Emulador
1. Click en menú: **Tools** → **Device Manager**
2. Verás lista de dispositivos virtuales
3. Click en botón **▶ (Play)** del dispositivo que quieras usar
4. **ESPERA 2-3 MINUTOS** a que inicie completamente
   - Verás pantalla de Android que carga
   - Luego "ANDROID" en la pantalla de bloqueo
   - Desliza para desbloquear

### Paso 3: Compilar e Instalar
1. Click en botón **"Run"** en Android Studio
   - O presiona: **Shift + F10**
2. Se abrirá ventana "Select Deployment Target"
3. Selecciona tu emulador
4. Click **"OK"**
5. Android Studio compilará e instalará

### Paso 4: Ver la app corriendo
- La app debería abrir automáticamente en el emulador
- Verás la pantalla con la lista de plantas

---

## ✅ OPCIÓN 2: Ejecutar desde Línea de Comandos

### En Windows:

```batch
# 1. Abre terminal PowerShell como administrador
# 2. Ve al directorio del proyecto:
cd "C:\Users\tu_usuario\Documents\AppPlantas2"

# 3. Ejecuta el script automático:
.\run_debug.bat
```

### En macOS/Linux:

```bash
# 1. Abre terminal
# 2. Ve al directorio del proyecto:
cd ~/Documents/AppPlantas2

# 3. Ejecuta el script automático:
bash run_debug.sh
```

---

## 🔧 Solución de Problemas

### Problema 1: "No hay emulador/dispositivo"

**Solución:**
1. Abre **Android Studio**
2. Click en **Device Manager** (Tools → Device Manager)
3. Si no hay dispositivos:
   - Click en **"Create Device"**
   - Selecciona modelo (ej: Pixel 6)
   - Click **"Next"** hasta crear
4. Click en **Play (▶)** para iniciar
5. ESPERA 2-3 MINUTOS a que cargue

### Problema 2: Error de compilación

**Solución:**
1. Abre terminal
2. Ejecuta:
   ```
   cd C:\Users\root\Documents\AppPlantas2
   .\gradlew clean
   .\gradlew build
   ```
3. Si sigue errando, envía el mensaje de error

### Problema 3: "App no inicia"

**Solución:**
1. Abre terminal
2. Ejecuta:
   ```
   adb logcat | grep PlantActivity
   ```
3. Verás el error en los logs

---

## 📱 Pruebas Básicas

Una vez que la app esté abierta en el emulador:

### Test 1: Cámara
1. Click en botón **"Cámara"**
2. Toma una foto de la pantalla/cualquier cosa
3. Debería mostrar "Analizando imagen..."
4. Luego resultado o sugerencias

### Test 2: Galería
1. Click en botón **"Galería"**
2. Selecciona una imagen cualquiera
3. Debería procesar y mostrar resultado

### Test 3: Agregar Planta
1. Click en botón **"Agregar"**
2. Completa formulario
3. Click "Guardar"
4. Debería aparecer en lista

### Test 4: Buscar
1. Usa barra de búsqueda
2. Escribe nombre de planta (ej: "Rosa")
3. Debería filtrar la lista

---

## 🚀 Comando Directo (Avanzado)

Si quieres hacerlo manualmente:

```powershell
# 1. Compilar
cd C:\Users\root\Documents\AppPlantas2
.\gradlew assembleDebug

# 2. Instalar
$env:ANDROID_HOME = "C:\Users\tu_usuario\AppData\Local\Android\Sdk"
& "$env:ANDROID_HOME\platform-tools\adb.exe" install -r `
  "app\build\outputs\apk\debug\app-debug.apk"

# 3. Ejecutar
& "$env:ANDROID_HOME\platform-tools\adb.exe" shell am start `
  -n com.mx.plantas/.MainActivity
```

---

## ✨ Checklist de Ejecución

```
☐ 1. Android Studio instalado
☐ 2. Emulador instalado (Device Manager)
☐ 3. Emulador ABIERTO y completamente cargado
☐ 4. Proyecto AppPlantas2 abierto en Studio
☐ 5. Click "Run" o Shift+F10
☐ 6. Seleccionar emulador en ventana emergente
☐ 7. Esperar a que compile (30-60 segundos)
☐ 8. App abre en emulador
☐ 9. ¡Probar cámara, galería, búsqueda!
```

---

## 📊 Flujo Completo Visual

```
┌─────────────────────────────────────┐
│  Android Studio (proyecto abierto)  │
└────────────────┬────────────────────┘
                 │
                 ▼
        ┌─────────────────┐
        │ Abre Device Mgr │ (Tools → Device Manager)
        └────────┬────────┘
                 │
                 ▼
        ┌─────────────────────────┐
        │ Emulador inicia (▶ Play)│
        └────────┬────────────────┘
                 │
         (ESPERA 2-3 MIN)
                 │
                 ▼
        ┌──────────────────────┐
        │ Emulador listo       │
        │ (pantalla cargada)   │
        └────────┬─────────────┘
                 │
                 ▼
        ┌──────────────────────┐
        │ Click "Run" Studio   │ (o Shift+F10)
        │ o ejecuta script     │
        └────────┬─────────────┘
                 │
                 ▼
        ┌──────────────────────┐
        │ Compila app (~30s)   │
        └────────┬─────────────┘
                 │
                 ▼
        ┌──────────────────────┐
        │ Instala en emulador  │
        └────────┬─────────────┘
                 │
                 ▼
        ┌──────────────────────┐
        │ 🌿 AppPlantas2 CORRE │
        │ ¡Prueba las funciones│
        └──────────────────────┘
```

---

## 📞 Si Nada Funciona

1. **Verifica que tienes Android SDK instalado:**
   ```
   echo %ANDROID_HOME%
   ```
   Debería mostrar ruta como: `C:\Users\...\AppData\Local\Android\Sdk`

2. **Verifica que gradlew existe:**
   ```
   dir gradlew.bat
   ```

3. **Intenta compilar manualmente:**
   ```
   .\gradlew build
   ```

4. **Si ves errores, ejecuta:**
   ```
   .\gradlew --info build 2>&1 | head -100
   ```

5. **Si nada funciona, envía estos logs:**
   ```
   .\gradlew build > build_log.txt 2>&1
   ```

---

## 🎓 Resumen

| Paso | Acción | Tiempo |
|------|--------|--------|
| 1 | Abrir Device Manager | 10 seg |
| 2 | Iniciar emulador | 2-3 min |
| 3 | Click Run en Studio | 5 seg |
| 4 | Compilar | 30-60 seg |
| 5 | Instalar | 10-20 seg |
| 6 | App abre | 5 seg |
| **TOTAL** | | **~4-5 min** |

---

## ✅ Conclusión

Si sigues estos pasos, **la app debería correr sin problemas**.

**Lo más importante:** Asegúrate de que el emulador esté completamente cargado antes de presionar Run.

¡Si tienes problemas, avísame! 🚀
