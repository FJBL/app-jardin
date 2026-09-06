# 🚀 Guía: Cómo Ejecutar AppPlantas2

## El Problema: No Aparece Botón de "Run"

Esto es **normal** si:
- No hay emulador/dispositivo conectado
- Android Studio no puede detectar dónde ejecutar la app

---

## ✅ Soluciones

### Opción 1: Usar Android Studio (Recomendado)

#### Paso 1: Abrir Dispositivo
1. **En Android Studio**, ve a:
   ```
   Tools → Device Manager
   ```

2. **Selecciona un emulador** (o crea uno nuevo):
   - Si existe uno: Click en ▶️ (Play)
   - Si no existe: Click en "Create Virtual Device"

3. **Espera a que inicie** (2-3 minutos la primera vez)

#### Paso 2: Ejecutar desde Android Studio
1. En la barra superior, verifica que diga:
   ```
   app [com.mx.plantas]
   ```

2. Click en ▶️ **Run** (debería aparecer si hay dispositivo)
   - O presiona: `Shift + F10`

3. **Selecciona el dispositivo** que abriste y click OK

4. ¡La app se compilará, instalará y ejecutará! 🎉

---

### Opción 2: Usar Línea de Comandos

#### Método A: Script Automático (Windows)
```batch
# Navega a la carpeta del proyecto
cd "c:\Users\root\Documents\AppPlantas2"

# Ejecuta el script
run_app.bat
```

El script hará todo automáticamente:
- ✅ Verifica dispositivo
- ✅ Compila la app
- ✅ Instala en dispositivo
- ✅ Lanza la app

#### Método B: Comandos Manuales
```bash
cd c:\Users\root\Documents\AppPlantas2

# 1. Compilar
.\gradlew.bat assembleDebug

# 2. Instalar
.\gradlew.bat installDebug

# 3. Lanzar
adb shell am start -n com.mx.plantas/.MainActivity
```

---

## 📱 Verificar Dispositivo Disponible

Abre PowerShell y ejecuta:
```powershell
adb devices
```

Deberías ver algo como:
```
List of attached devices
emulator-5554          device
```

Si ves **"List of attached devices"** sin nada abajo:
- No hay emulador/dispositivo ejecutándose
- Ve al paso 1 para abrir Device Manager

---

## 🛠️ Crear Emulador (si no lo tienes)

1. **Android Studio → Tools → Device Manager**

2. **Click "Create Virtual Device"**

3. **Selecciona:**
   - Phone model: Pixel 6 (o similar)
   - System Image: Android 14 (API 34)
   - Click "Next" → "Finish"

4. **Abre el emulador** que creaste (click ▶️)

5. Espera a que inicie (puede tardar 2-3 minutos)

6. Regresa a Android Studio y haz click en ▶️ **Run**

---

## 🔍 Si Aún No Funciona

### Verificar Instalación de ADB
```powershell
# Verificar que adb está disponible
adb version

# Debería mostrar: Android Debug Bridge version X.X.X
```

Si da error:
1. Ve a Android Studio
2. Tools → SDK Manager
3. En la pestaña "SDK Tools"
4. Asegúrate de que "Android SDK Platform-Tools" está instalado

### Limpiar e Intentar Nuevamente
```powershell
cd "c:\Users\root\Documents\AppPlantas2"

# Limpia archivos de compilación
.\gradlew.bat clean

# Compila nuevamente
.\gradlew.bat build
```

### Desinstalar Versión Anterior
```powershell
adb uninstall com.mx.plantas
```

Luego intenta instalar nuevamente.

---

## 📋 Checklist de Verificación

Antes de ejecutar, verifica:

- [ ] Android Studio está abierto
- [ ] Emulador está ejecutándose (ve a Device Manager)
  - O dispositivo físico está conectado por USB
- [ ] SDK Tools está instalado (Tools → SDK Manager)
- [ ] `adb devices` muestra un dispositivo activo
- [ ] El proyecto está abierto en Android Studio
- [ ] Sin errores visibles (panel derecho)

---

## 🎯 Una Vez que Funcione

1. Haz click en ▶️ **Run** (o Shift + F10)
2. Verá: "Waiting for debugger..."
3. La app se instalará y ejecutará automáticamente
4. ¡Disfruta la identificación de plantas! 🌿

---

## 📞 Errores Comunes

| Error | Solución |
|-------|----------|
| "No devices found" | Abre emulador en Device Manager o conecta teléfono |
| "adb: command not found" | Instala Android SDK Platform-Tools |
| "Cannot find app" | Haz `.\gradlew.bat clean build` |
| "Compilation error" | Verifica que compiló exitosamente (arriba se vio ✅) |

---

## ✨ Tips

- **Emulador lento**: Usa Pixel 6 o inferior, API 30-34
- **Primera vez tarda**: Es normal, próximas veces son más rápidas
- **Mantén emulador abierto**: No lo cierres entre runs
- **Atajo teclado**: `Shift + F10` = Run
- **Cambiar dispositivo**: Click en selector de dispositivos (arriba)
