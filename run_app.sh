#!/bin/bash
# Script para ejecutar AppPlantas2

echo "================================================"
echo "  🌿 AppPlantas2 - Script de Ejecución"
echo "================================================"
echo ""

cd "c:\Users\root\Documents\AppPlantas2"

echo "📋 Paso 1: Verificar emulador/dispositivo conectado..."
echo ""

# Verificar si hay dispositivos conectados
devices=$(adb devices 2>/dev/null | grep -v "^List of" | grep -v "^$" | grep -v "offline")

if [ -z "$devices" ]; then
    echo "❌ NO HAY DISPOSITIVO/EMULADOR CONECTADO"
    echo ""
    echo "Opciones para ejecutar:"
    echo ""
    echo "1️⃣  ABRIR EMULADOR (si lo tienes instalado):"
    echo "   En Android Studio: Tools > Device Manager > Click en emulador"
    echo "   O en terminal: \$ANDROID_HOME/emulator/emulator -avd 'nombre_emulador'"
    echo ""
    echo "2️⃣  CONECTAR DISPOSITIVO FÍSICO:"
    echo "   - Conecta teléfono por USB"
    echo "   - Activa 'Depuración USB' en configuración del teléfono"
    echo "   - Verifica conexión: adb devices"
    echo ""
    echo "3️⃣  CREAR EMULADOR EN ANDROID STUDIO:"
    echo "   Tools > Device Manager > Create Virtual Device"
    echo ""
else
    echo "✅ Dispositivos encontrados:"
    echo "$devices"
    echo ""
    
    echo "📋 Paso 2: Compilar aplicación..."
    .\gradlew.bat assembleDebug --no-daemon -q
    
    if [ $? -eq 0 ]; then
        echo "✅ Compilación exitosa"
        echo ""
        echo "📋 Paso 3: Instalar en dispositivo..."
        .\gradlew.bat installDebug --no-daemon
        
        if [ $? -eq 0 ]; then
            echo "✅ Instalación exitosa"
            echo ""
            echo "📋 Paso 4: Lanzar aplicación..."
            adb shell am start -n com.mx.plantas/.MainActivity
            echo ""
            echo "✅ App lanzada!"
        else
            echo "❌ Error en instalación"
        fi
    else
        echo "❌ Error en compilación"
    fi
fi
