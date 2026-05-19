package com.transflow.adas.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.transflow.adas.camera.CameraAnalyzer
import com.transflow.adas.data.AppDatabase
import com.transflow.adas.data.EventRepository
import com.transflow.adas.databinding.ActivityMainBinding
import com.transflow.adas.location.SpeedTracker
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cameraExecutor: ExecutorService
    private val cameraAnalyzer = CameraAnalyzer()
    private lateinit var speedTracker: SpeedTracker
    private lateinit var repository: EventRepository
    
    private var lastEventTime = 0L

    companion object {
        private const val TAG = "Transflow"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraExecutor = Executors.newSingleThreadExecutor()
        speedTracker = SpeedTracker(this)
        
        val db = AppDatabase.getDatabase(this)
        repository = EventRepository(db.eventDao())

        // Asignar el callback al analyzer para actualizar el overlay
        cameraAnalyzer.onResults = { results ->
            runOnUiThread {
                binding.overlay.setResults(results)
                
                val hasCriticalRisk = results.any { it.isCritical }
                if (hasCriticalRisk) {
                    val currentTime = System.currentTimeMillis()
                    // Cooldown de 3 segundos para no spamear la base de datos
                    if (currentTime - lastEventTime > 3000) {
                        lastEventTime = currentTime
                        saveCriticalEvent()
                    }
                }
            }
        }

        // Asignar el callback del GPS
        speedTracker.onSpeedChanged = { speed ->
            runOnUiThread {
                binding.tvSpeed.text = "${speed.toInt()} km/h"
                cameraAnalyzer.currentSpeed = speed
            }
        }

        if (allPermissionsGranted()) {
            startCamera()
            speedTracker.startTracking()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun saveCriticalEvent() {
        // Ejecutamos en una corrutina de Room (I/O)
        lifecycleScope.launch {
            repository.recordEvent(
                eventType = "COLLISION_WARNING",
                speedKmh = cameraAnalyzer.currentSpeed,
                description = "Vehículo detectado demasiado cerca en el carril"
            )
            Log.d(TAG, "Evento crítico guardado en Room DB")
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            // ImageAnalyzer
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, cameraAnalyzer)
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer
                )

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
                speedTracker.startTracking()
            } else {
                Toast.makeText(this, "Permisos denegados.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        speedTracker.stopTracking()
    }
}
