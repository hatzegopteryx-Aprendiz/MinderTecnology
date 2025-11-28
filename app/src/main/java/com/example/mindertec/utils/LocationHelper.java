package com.example.mindertec.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationHelper {
    private FusedLocationProviderClient fusedLocationClient;
    private Context context;
    private LocationCallback locationCallback;

    public interface LocationCallbackListener {
        void onLocationReceived(String locationString, double latitude, double longitude);
        void onLocationError(String errorMessage);
    }

    public LocationHelper(Context context) {
        this.context = context;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public void getCurrentLocation(LocationCallbackListener listener) {
        // Verificar permisos
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            listener.onLocationError("Permisos de ubicación no concedidos");
            return;
        }

        // Verificar si los servicios de ubicación están disponibles
        try {
            LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                    .setWaitForAccurateLocation(false)
                    .setMinUpdateIntervalMillis(5000)
                    .setMaxUpdateDelayMillis(10000)
                    .build();

            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null || locationResult.getLastLocation() == null) {
                        listener.onLocationError("No se pudo obtener la ubicación");
                        return;
                    }

                    Location location = locationResult.getLastLocation();
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    // Obtener dirección legible
                    String locationString = getAddressFromLocation(latitude, longitude);
                    
                    // Detener actualizaciones
                    fusedLocationClient.removeLocationUpdates(locationCallback);
                    
                    listener.onLocationReceived(locationString, latitude, longitude);
                }
            };

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

            // Timeout después de 15 segundos
            new android.os.Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (locationCallback != null) {
                    fusedLocationClient.removeLocationUpdates(locationCallback);
                    listener.onLocationError("Tiempo de espera agotado al obtener ubicación");
                }
            }, 15000);

        } catch (SecurityException e) {
            listener.onLocationError("Error de seguridad al acceder a la ubicación: " + e.getMessage());
        } catch (Exception e) {
            listener.onLocationError("Error al obtener ubicación: " + e.getMessage());
        }
    }

    private String getAddressFromLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                StringBuilder addressString = new StringBuilder();
                
                // Construir dirección legible
                if (address.getThoroughfare() != null) {
                    addressString.append(address.getThoroughfare());
                }
                if (address.getSubThoroughfare() != null) {
                    addressString.append(" ").append(address.getSubThoroughfare());
                }
                if (address.getLocality() != null) {
                    if (addressString.length() > 0) addressString.append(", ");
                    addressString.append(address.getLocality());
                }
                if (address.getAdminArea() != null) {
                    if (addressString.length() > 0) addressString.append(", ");
                    addressString.append(address.getAdminArea());
                }
                
                if (addressString.length() > 0) {
                    return addressString.toString();
                } else {
                    return String.format(Locale.getDefault(), "%.6f, %.6f", latitude, longitude);
                }
            } else {
                return String.format(Locale.getDefault(), "%.6f, %.6f", latitude, longitude);
            }
        } catch (IOException e) {
            // Si falla la geocodificación, devolver coordenadas
            return String.format(Locale.getDefault(), "%.6f, %.6f", latitude, longitude);
        }
    }

    public void cleanup() {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
            locationCallback = null;
        }
    }
}

