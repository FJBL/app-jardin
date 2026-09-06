@echo off
REM run_debug.bat - Script para compilar e instalar AppPlantas2 en emulador Windows

echo.
echo ╔════════════════════════════════════════════════════════════════════════════╗
echo ║                    🌿 AppPlantas2 - Ejecutar App                          ║
echo ╚════════════════════════════════════════════════════════════════════════════╝
echo.

REM 1. Cambiar a directorio del proyecto
cd /d "%~dp0"
echo 📁 Directorio: %CD%
echo.

REM 2. Compilar con gradlew
echo 🔨 Compilando app...
call gradlew.bat assembleDebug
if errorlevel 1 (
    echo ❌ Error en compilación
    pause
    exit /b 1
)
echo ✓ Compilación exitosa
echo.

REM 3. Verificar APK
echo 📦 Verificando APK...
if not exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo ❌ APK no encontrado
    pause
    exit /b 1
)
echo ✓ APK encontrado
echo.

REM 4. Usar adb.exe si está disponible
if defined ANDROID_HOME (
    set ADB=%ANDROID_HOME%\platform-tools\adb.exe
) else (
    echo ⚠️  ANDROID_HOME no configurado
    echo.
    echo 📱 Abre Android Studio y:
    echo    1. Click en Device Manager
    echo    2. Selecciona un dispositivo
    echo    3. Haz click en botón Play (▶) para iniciarlo
    echo    4. Espera a que cargue (2-3 minutos)
    echo    5. Luego presiona Shift+F10 para instalar y ejecutar
    pause
    exit /b 1
)

echo 📱 Verificando dispositivo...
"%ADB%" devices > nul 2>&1
if errorlevel 1 (
    echo ❌ Emulador/dispositivo no disponible
    echo.
    echo 📱 Abre el emulador en Android Studio:
    echo    1. Tools → Device Manager
    echo    2. Click Play (▶)
    echo    3. Espera a que inicie
    pause
    exit /b 1
)

echo ✓ Dispositivo conectado
echo.

REM 5. Instalar
echo 📥 Instalando app en dispositivo...
"%ADB%" install -r "app\build\outputs\apk\debug\app-debug.apk"
if errorlevel 1 (
    echo ❌ Error instalando app
    pause
    exit /b 1
)
echo ✓ App instalada
echo.

REM 6. Iniciar
echo 🚀 Iniciando app...
"%ADB%" shell am start -n com.mx.plantas/.MainActivity
if errorlevel 1 (
    echo ⚠️  Error iniciando app (pero podría estar instalada)
    echo    Abre la app desde el menú del emulador
)

echo.
echo ✅ ¡Listo! Verifica la pantalla del emulador
echo.
pause
