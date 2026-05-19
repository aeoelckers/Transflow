package com.transflow.adas.engine

import android.graphics.RectF

class AlertManager {

    enum class AlertLevel {
        NONE, WARNING, CRITICAL
    }

    data class AlertStatus(
        val collisionLevel: AlertLevel,
        val laneDepartureLevel: AlertLevel
    )

    /**
     * Evalúa el riesgo de colisión basado de forma heurística.
     * @param boundingBox La caja del vehículo detectado frente a nosotros.
     * @param screenWidth Ancho de la imagen procesada.
     * @param screenHeight Alto de la imagen procesada.
     * @param speedKmh Velocidad actual del camión.
     */
    fun evaluateCollisionRisk(
        boundingBox: RectF,
        screenWidth: Int,
        screenHeight: Int,
        speedKmh: Float
    ): AlertLevel {
        // Heurística 1: Calcular cuánto ocupa el vehículo de nuestra pantalla (área)
        val boxArea = boundingBox.width() * boundingBox.height()
        val screenArea = screenWidth * screenHeight
        val coverageRatio = boxArea / screenArea

        // Heurística 2: ¿Está en nuestro carril? (Su centro está cerca del centro inferior)
        val boxCenterX = boundingBox.centerX()
        val isCentered = boxCenterX > screenWidth * 0.3f && boxCenterX < screenWidth * 0.7f
        
        // Si no está centrado en nuestro carril, el riesgo es menor
        if (!isCentered) return AlertLevel.NONE

        // Umbrales basados en velocidad (a mayor velocidad, toleramos menos cercanía)
        val criticalRatio = if (speedKmh > 80f) 0.15f else 0.25f
        val warningRatio = if (speedKmh > 80f) 0.08f else 0.12f

        return when {
            coverageRatio >= criticalRatio -> AlertLevel.CRITICAL
            coverageRatio >= warningRatio -> AlertLevel.WARNING
            else -> AlertLevel.NONE
        }
    }
}
