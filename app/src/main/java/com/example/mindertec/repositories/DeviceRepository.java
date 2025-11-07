package com.example.mindertec.repositories;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.mindertec.auth.session_manager_screen;
import com.example.mindertec.models.Device;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.Task;

public class DeviceRepository {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private session_manager_screen sessionManager;

    public DeviceRepository(Context context) {
        this.mAuth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        this.sessionManager = new session_manager_screen(context);
    }

    public interface SaveDeviceCallback {
        void onSuccess(String deviceId);
        void onError(String errorMessage);
    }

    public void saveDevice(Device device, SaveDeviceCallback callback) {
        if (mAuth.getCurrentUser() == null) {
            callback.onError("Error: Usuario no autenticado. Por favor, inicia sesión");
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        String deviceId = mDatabase.child("Dispositivos").child(userId).push().getKey();
        
        if (deviceId == null) {
            callback.onError("Error al generar ID del dispositivo");
            return;
        }

        device.setDeviceId(deviceId);

        // Guardar en Firebase Database
        mDatabase.child("Dispositivos").child(userId).child(deviceId)
                .setValue(device)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(deviceId);
                    } else {
                        String errorMessage = translateFirebaseError(task.getException());
                        callback.onError(errorMessage);
                    }
                });
    }

    private String translateFirebaseError(Exception exception) {
        if (exception == null) {
            return "Error desconocido al guardar dispositivo";
        }
        
        String exceptionMessage = exception.getMessage();
        if (exceptionMessage == null) {
            return "Error desconocido al guardar dispositivo";
        }
        
        if (exceptionMessage.contains("network") || exceptionMessage.contains("Network")) {
            return "Error de conexión. Verifica tu internet";
        } else if (exceptionMessage.contains("permission") || exceptionMessage.contains("Permission")) {
            return "No tienes permiso para guardar dispositivos";
        } else if (exceptionMessage.contains("timeout") || exceptionMessage.contains("Timeout")) {
            return "Tiempo de espera agotado. Intenta nuevamente";
        } else {
            return "Error al guardar: " + exceptionMessage;
        }
    }
}

