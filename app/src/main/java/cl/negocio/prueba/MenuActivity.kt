package cl.negocio.prueba

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.widget.TextView


class MenuActivity : AppCompatActivity() {

    private val locationPermissionCode = 1000
    private val backgroundLocationPermissionCode = 1001
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)

        // Configuración del diseño existente
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializa el cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Verifica permisos de ubicación
        checkLocationPermission()
    }

    private fun showLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    // Si la ubicación es obtenida, mostramos en el TextView
                    val locationTextView = findViewById<TextView>(R.id.locationTextView)
                    locationTextView.text = "La ubicación del dispositivo es:\nLatitud: ${location.latitude}, Longitud: ${location.longitude}"
                } else {
                    // Si la ubicación es nula, mostrar un mensaje alternativo
                    val locationTextView = findViewById<TextView>(R.id.locationTextView)
                    locationTextView.text = "No se pudo obtener la ubicación."
                }
            }
        } else {
            // Si el permiso no está concedido, solicitarlo de nuevo
            checkLocationPermission()
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        } else {
            checkBackgroundLocationPermission() // Si ya tiene el permiso en primer plano
            showLocation()
        }
    }

    private fun checkBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), backgroundLocationPermissionCode)
            } else {
                startLocationService() // Si ya tiene todos los permisos, iniciamos el servicio
            }
        } else {
            startLocationService()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            locationPermissionCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkBackgroundLocationPermission()
                    showLocation()
                } else {
                    // Permiso denegado, mostrar un mensaje o deshabilitar la funcionalidad
                    val locationTextView = findViewById<TextView>(R.id.locationTextView)
                    locationTextView.text = "Permiso de ubicación denegado."
                }
            }
            backgroundLocationPermissionCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationService()
                } else {
                    // Permiso denegado, mostrar un mensaje o deshabilitar la funcionalidad
                }
            }
        }
    }

    private fun startLocationService() {
        val serviceIntent = Intent(this, LocationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }
}