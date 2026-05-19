# Transflow 🚛📸

**Transflow** es un Sistema Avanzado de Asistencia al Conductor (ADAS) de bajo costo para vehículos comerciales, diseñado para operar directamente desde un teléfono Android estándar montado en el tablero del vehículo.

El sistema asiste al conductor monitoreando la ruta en tiempo real, utilizando procesamiento de inteligencia artificial local (offline) y sensores del dispositivo para prevenir posibles colisiones y salidas de carril.

## 🚀 Características Principales (MVP)

*   **Forward Collision Warning (FCW):** Detecta heurísticamente si el vehículo que va delante está demasiado cerca basándose en la velocidad actual y el tamaño del vehículo detectado.
*   **Lane Departure Warning (LDW):** *(Estructura lista)* Analiza las líneas de la pista para emitir advertencias de desvío del carril central.
*   **Velocímetro en Tiempo Real:** Interfaz simple estilo *dashcam* que muestra la velocidad actual obtenida mediante GPS (`LocationManager`).
*   **Registro de Eventos Críticos:** Guarda de forma local y silenciosa un registro de frenadas bruscas o alertas de colisión en una base de datos local (`Room Database`) para auditorías de la flota.
*   **100% Offline y Low-cost:** Toda la inferencia corre en el procesador / NPU del teléfono usando **TensorFlow Lite**, sin depender de conexión a internet para funcionar.

## 🛠️ Stack Tecnológico

*   **Lenguaje:** Kotlin Nativo (Android)
*   **Arquitectura:** MVVM (Model-View-ViewModel)
*   **Visión Computacional:** CameraX (Google Jetpack) para extracción de frames de bajo consumo.
*   **Inteligencia Artificial:** TensorFlow Lite (YOLOv8n int8).
*   **Base de Datos:** Room (SQLite)

## 🏗️ Estructura del Proyecto

```text
com.transflow.adas
├── ui/              # Interfaz de usuario Dashcam (MainActivity) y Componentes UI (OverlayView)
├── camera/          # Lógica de CameraX (CameraAnalyzer para extracción de frames)
├── ai/              # Detección de IA (ObjectDetector para TFLite y LaneDetector)
├── engine/          # Heurística de Riesgos y Colisiones (AlertManager)
├── location/        # Tracking de Velocidad GPS (SpeedTracker)
└── data/            # Persistencia de eventos (AppDatabase, EventDao, EventRepository)
```

## 📥 Cómo compilar e instalar

1. Clona el repositorio y ábrelo en **Android Studio**.
2. Deja que Gradle sincronice las dependencias (`CameraX`, `TensorFlow Lite`, `Room`, `Coroutines`).
3. **Paso Crucial:** El proyecto requiere el modelo de IA para detectar vehículos. Debes exportar tu modelo YOLO (ej. `yolov8n.pt` exportado a TFLite) y colocar el archivo `yolov8n.tflite` dentro de la carpeta `app/src/main/assets/`.
4. Conecta un dispositivo físico Android (no uses emulador, ya que se requiere la cámara y aceleración de IA por hardware).
5. Compila y ejecuta la app.
6. Acepta los permisos de Cámara y Localización, monta el teléfono horizontalmente en el tablero y comienza el viaje.

## 🔮 Roadmap (Funcionalidades Futuras)
*   **Telemetría a la Nube:** Sincronización de base de datos de eventos hacia **Supabase** cuando el dispositivo recupere conexión Wi-Fi, creando perfiles de riesgo ("Scoring") de choferes.
*   **Buffer Circular de Dashcam:** Alerta visual y guardado automático de un clip de video (10s antes y después) de un evento de colisión crítica.
*   **Detección de Fatiga Frontal:** Si el hardware lo soporta, uso de cámara delantera en baja resolución para detectar si el conductor cierra los ojos excesivamente.
