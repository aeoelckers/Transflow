package com.transflow.adas.camera

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.transflow.adas.ai.DetectionResult
import com.transflow.adas.ai.ObjectDetector
import com.transflow.adas.engine.AlertManager

class CameraAnalyzer : ImageAnalysis.Analyzer {

    var onResults: ((List<DetectionResult>) -> Unit)? = null
    var currentSpeed: Float = 0f // Actualizada desde la Activity
    
    private val detector = ObjectDetector()
    private val alertManager = AlertManager()

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            
            // Simular detección de objetos (YOLO)
            val dummyResults = detector.simulateDetection(imageProxy.width, imageProxy.height)
            
            // Evaluar alertas por cada objeto detectado usando el AlertManager
            val evaluatedResults = dummyResults.map { result ->
                val riskLevel = alertManager.evaluateCollisionRisk(
                    result.boundingBox, 
                    imageProxy.width, 
                    imageProxy.height, 
                    currentSpeed
                )
                // Marcamos como crítico si el nivel es Warning o Critical
                val isCritical = riskLevel == AlertManager.AlertLevel.CRITICAL
                result.copy(isCritical = isCritical)
            }

            onResults?.invoke(evaluatedResults)
        }
        
        imageProxy.close()
    }
}
