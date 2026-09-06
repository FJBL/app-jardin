@echo off
REM Script para ejecutar AppPlantas2
echo.
echo ================================================
echo   ^>^> AppPlantas2 - Ejecucion de Aplicacion
echo ================================================
echo.

cd /d "c:\Users\root\Documents\AppPlantas2"

echo [1] Verificando conexion de dispositivos...
echo.

adb devices > nul 2>&1
if %errorlevel% neq 0 (
    echo ^!^! ERROR: adb no encontrado
    echo.
    echo Asegúrate de que Android SDK esté instalado y configurado.
    pause
    exit /b 1
)

for /f "skip=1" %%A in ('adb devices') do (
    set "line=%%A"
    if not "!line!"=="" (
        if not "!line:~-7!"=="offline" (
            set "device=!line:~0,20!"
            goto :found_device
        )
    )
)

echo XX Ningun dispositivo/emulador conectado
echo.
echo SOLUCIONES:
echo.
echo [1] INICIAR EMULADOR (Android Studio):
echo     - Abre Android Studio
echo     - Tools ^> Device Manager
echo     - Selecciona un emulador y presiona Play
echo.
echo [2] CONECTAR DISPOSITIVO FISICO:
echo     - Conecta tu telefono por USB
echo     - Activa "Depuracion USB" en Configuracion
echo     - Verifica: adb devices
echo.
echo [3] CREAR EMULADOR:
echo     - Android Studio: Tools ^> Device Manager ^> Create Device
echo.
pause
exit /b 1

:found_device
echo OK Dispositivo encontrado: %device%
echo.

echo [2] Compilando aplicacion...
call .\gradlew.bat clean assembleDebug --no-daemon -q 2>&1 | findstr /V "^$"
if %errorlevel% neq 0 (
    echo XX Error en compilacion
    pause
    exit /b 1
)
echo OK Compilacion exitosa
echo.

echo [3] Instalando en dispositivo...
call .\gradlew.bat installDebug --no-daemon -q 2>&1 | findstr /V "^$"
if %errorlevel% neq 0 (
    echo XX Error en instalacion
    pause
    exit /b 1
)
echo OK Instalacion exitosa
echo.

echo [4] Lanzando aplicacion...
adb shell am start -n com.mx.plantas/.MainActivity
if %errorlevel% neq 0 (
    echo XX Error al lanzar la app
    pause
    exit /b 1
)
echo.
echo OK ^>^> Aplicacion ejecutandose! ^>^>
echo.
pause
