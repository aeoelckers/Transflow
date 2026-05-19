package com.transflow.adas.ai

import com.transflow.adas.engine.AlertManager

class LaneDetector {

    /**
     * Evalúa heurísticamente si el vehículo se está saliendo del carril.
     * En una implementación real, esto usaría OpenCV (Canny Edge + Hough)
     * para buscar las líneas blancas/amarillas en la mitad inferior de la pantalla.
     */
    fun evaluateLaneDeparture(width: Int, height: Int): AlertManager.AlertLevel {
        // TODO: OpenCV logic here
        
        // Simulación: Si es un MVP básico, podemos asumir que siempre estamos en el carril
        // a menos que el usuario "haga un giro brusco" (lo cual podríamos leer del acelerómetro).
        // Por ahora, retornamos NONE.
        return AlertManager.AlertLevel.NONE
    }
}
