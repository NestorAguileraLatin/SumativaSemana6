package cl.negocio.prueba;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class LocationService extends Service {

    private FusedLocationProviderClient fusedLocationClient;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override

    public int onStartCommand(Intent intent, int flags, int startId) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Verificar permisos de ubicación
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            try {
                // Obtener la última ubicación conocida
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    // La ubicación no es nula, usarla aquí
                                    Log.d("LocationService", "Ubicación: " + location.getLatitude() + ", " + location.getLongitude());

                                    // Llamar a saveLocationToFirebase para guardar la ubicación
                                    saveLocationToFirebase(location.getLatitude(), location.getLongitude());
                                } else {
                                    // La ubicación es nula
                                    Log.d("LocationService", "Ubicación nula");
                                }
                            }
                        });
            } catch (SecurityException e) {
                // Manejar la excepción SecurityException
                Log.e("LocationService", "Error de seguridad al obtener la ubicación", e);
            }
        } else {
            // No tienes permisos de ubicación, manejar esto (solicitar permisos, mostrar mensaje, etc.)
            Log.w("LocationService", "No tienes permisos de ubicación");
        }
        return START_STICKY;
    }

    private void saveLocationToFirebase(double latitude, double longitude) {
        Log.d("LocationService", "Guardando ubicación: " + latitude + ", " + longitude);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        LocationData locationData = new LocationData(latitude, longitude);

        db.collection("locations")
                .add(locationData)
                .addOnSuccessListener(documentReference -> Log.d("Firebase", "Location saved"))
                .addOnFailureListener(e -> Log.e("Firebase", "Error saving location", e));
    }

    // POJO para almacenar la ubicaciÃ³n
    public static class LocationData {
        private double latitude;
        private double longitude;

        public LocationData(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }
    }
}




