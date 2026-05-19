package com.transflow.adas.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.transflow.adas.ai.DetectionResult

class OverlayView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var results: List<DetectionResult> = listOf()
    private val boxPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 5f
    }
    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 40f
        style = Paint.Style.FILL
        setShadowLayer(5.0f, 0f, 0f, Color.BLACK)
    }

    fun setResults(detectionResults: List<DetectionResult>) {
        results = detectionResults
        invalidate() // Redibuja la vista
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (result in results) {
            val boundingBox = result.boundingBox

            // Mapear coordenadas si es necesario (depende de cómo se escale la imagen)
            // Aquí asumimos que boundingBox ya está escalado a las dimensiones de la View
            
            // Elegir color por nivel de alerta
            boxPaint.color = if (result.isCritical) Color.RED else Color.YELLOW

            canvas.drawRect(boundingBox, boxPaint)
            canvas.drawText(
                "${result.className} ${(result.confidence * 100).toInt()}%",
                boundingBox.left,
                boundingBox.top - 10f,
                textPaint
            )
        }
    }
}
