package com.transflow.adas.ai

import android.graphics.RectF

data class DetectionResult(
    val boundingBox: RectF,
    val className: String,
    val confidence: Float,
    val isCritical: Boolean = false // Si está muy cerca
)
