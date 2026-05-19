package com.transflow.adas.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle

class SpeedTracker(private val context: Context) {

    private var locationManager: LocationManager? = null
    var currentSpeedKmh: Float = 0f
        private set

    // Callback para actualizar la UI
    var onSpeedChanged: ((Float) -> Unit)? = null

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            // location.speed está en m/s. Multiplicamos por 3.6 para km/h
            if (location.hasSpeed()) {
                currentSpeedKmh = location.speed * 3.6f
                onSpeedChanged?.invoke(currentSpeedKmh)
            }
        }
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    @SuppressLint("MissingPermission")
    fun startTracking() {
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        
        val isGpsEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true
        if (isGpsEnabled) {
            locationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000L, // Actualizar cada 1 segundo
                1f,    // O cada 1 metro
                locationListener
            )
        }
    }

    fun stopTracking() {
        locationManager?.removeUpdates(locationListener)
    }
}
