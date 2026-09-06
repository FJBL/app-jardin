#!/bin/bash
# run_app.sh - Script para compilar e instalar AppPlantas2 en emulador

echo "╔════════════════════════════════════════════════════════════════════════════╗"
echo "║                     🌿 AppPlantas2 - Ejecutar App                          ║"
echo "╚════════════════════════════════════════════════════════════════════════════╝"
echo ""

# 1. Verificar que el emulador está corriendo
echo "📱 Paso 1: Verificando emulador..."
adb_path="$(find $ANDROID_HOME -name adb -type f 2>/dev/null | head -1)"

if [ -z "$adb_path" ]; then
    echo "❌ ADB no encontrado"
    echo ""
    echo "⚠️  Necesitas abrir el emulador primero:"
    echo "    1. Abre Android Studio"
    echo "    2. Tools → Device Manager"
    echo "    3. Click en Play (▶) para iniciar dispositivo"
    echo "    4. Espera a que inicie (2-3 minutos)"
    exit 1
fi

# Verificar dispositivos conectados
device_count=$("$adb_path" devices | grep -c "device$")
if [ "$device_count" -eq 0 ]; then
    echo "❌ No hay emulador/dispositivo conectado"
    echo ""
    echo "Abre el emulador en Android Studio y espera a que inicie completamente"
    exit 1
fi

echo "✓ Emulador/dispositivo detectado"
echo ""

# 2. Compilar
echo "🔨 Paso 2: Compilando app..."
cd "$(dirname "$0")"
./gradlew assembleDebug

if [ $? -ne 0 ]; then
    echo "❌ Error en compilación"
    exit 1
fi

echo "✓ Compilación exitosa"
echo ""

# 3. Instalar
echo "📥 Paso 3: Instalando en dispositivo..."
apk_path="app/build/outputs/apk/debug/app-debug.apk"

if [ ! -f "$apk_path" ]; then
    echo "❌ APK no encontrado: $apk_path"
    exit 1
fi

"$adb_path" install -r "$apk_path"

if [ $? -ne 0 ]; then
    echo "❌ Error instalando app"
    exit 1
fi

echo "✓ App instalada"
echo ""

# 4. Iniciar
echo "🚀 Paso 4: Iniciando app..."
"$adb_path" shell am start -n com.mx.plantas/.MainActivity

echo "✓ App iniciada"
echo ""
echo "✅ ¡Listo! Verifica la pantalla del emulador"
