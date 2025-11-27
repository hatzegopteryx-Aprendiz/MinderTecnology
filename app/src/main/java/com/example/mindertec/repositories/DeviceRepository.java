package com.example.mindertec.repositories;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.mindertec.auth.session_manager_screen;
import com.example.mindertec.models.CompletedTask;
import com.example.mindertec.models.Device;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

    public interface GetDeviceCallback {
        void onSuccess(Device device);
        void onError(String errorMessage);
    }

    public interface UpdateInterventionCallback {
        void onSuccess();
        void onError(String errorMessage);
    }

    public interface AddCompletedTaskCallback {
        void onSuccess();
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

    public void getDevice(String deviceId, GetDeviceCallback callback) {
        if (mAuth.getCurrentUser() == null) {
            callback.onError("Error: Usuario no autenticado. Por favor, inicia sesión");
            return;
        }

        if (deviceId == null || deviceId.isEmpty()) {
            callback.onError("Error: ID de dispositivo no válido");
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference deviceRef = mDatabase.child("Dispositivos").child(userId).child(deviceId);

        deviceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Device device = snapshot.getValue(Device.class);
                    if (device != null) {
                        // Asegurar que el deviceId esté establecido
                        if (device.getDeviceId() == null || device.getDeviceId().isEmpty()) {
                            device.setDeviceId(deviceId);
                        }
                        callback.onSuccess(device);
                    } else {
                        callback.onError("Error al cargar datos del dispositivo");
                    }
                } else {
                    callback.onError("Dispositivo no encontrado");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                String errorMessage = translateFirebaseError(error.toException());
                callback.onError("Error al leer datos: " + errorMessage);
            }
        });
    }

    public ValueEventListener getDeviceListener(String deviceId, GetDeviceCallback callback) {
        if (mAuth.getCurrentUser() == null) {
            callback.onError("Error: Usuario no autenticado. Por favor, inicia sesión");
            return null;
        }

        if (deviceId == null || deviceId.isEmpty()) {
            callback.onError("Error: ID de dispositivo no válido");
            return null;
        }

        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference deviceRef = mDatabase.child("Dispositivos").child(userId).child(deviceId);

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Device device = snapshot.getValue(Device.class);
                    if (device != null) {
                        // Asegurar que el deviceId esté establecido
                        if (device.getDeviceId() == null || device.getDeviceId().isEmpty()) {
                            device.setDeviceId(deviceId);
                        }
                        callback.onSuccess(device);
                    } else {
                        callback.onError("Error al cargar datos del dispositivo");
                    }
                } else {
                    callback.onError("Dispositivo no encontrado");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                String errorMessage = translateFirebaseError(error.toException());
                callback.onError("Error al leer datos: " + errorMessage);
            }
        };

        deviceRef.addValueEventListener(listener);
        return listener;
    }

    public void removeDeviceListener(String deviceId, ValueEventListener listener) {
        if (mAuth.getCurrentUser() != null && deviceId != null && !deviceId.isEmpty()) {
            String userId = mAuth.getCurrentUser().getUid();
            DatabaseReference deviceRef = mDatabase.child("Dispositivos").child(userId).child(deviceId);
            deviceRef.removeEventListener(listener);
        }
    }

    public void updateIntervention(String deviceId, String comentario, String fecha, String fechaIso, 
                                   String tecnicoId, String tecnicoEmail, String tecnicoNombre,
                                   UpdateInterventionCallback callback) {
        if (mAuth.getCurrentUser() == null) {
            callback.onError("Error: Usuario no autenticado. Por favor, inicia sesión");
            return;
        }

        if (deviceId == null || deviceId.isEmpty()) {
            callback.onError("Error: ID de dispositivo no válido");
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference deviceRef = mDatabase.child("Dispositivos").child(userId).child(deviceId);

        // Actualizar solo los campos de intervención
        java.util.HashMap<String, Object> updates = new java.util.HashMap<>();
        updates.put("ultimaIntervencion", comentario);
        updates.put("ultimaIntervencionId", tecnicoId);
        updates.put("ultimaIntervencionEmail", tecnicoEmail);
        updates.put("ultimaIntervencionNombre", tecnicoNombre);
        updates.put("ultimaIntervencionFecha", fechaIso);

        deviceRef.updateChildren(updates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess();
            } else {
                String errorMessage = translateFirebaseError(task.getException());
                callback.onError("Error al actualizar intervención: " + errorMessage);
            }
        });
    }

    public void addCompletedTask(String deviceId, CompletedTask completedTask, AddCompletedTaskCallback callback) {
        if (mAuth.getCurrentUser() == null) {
            callback.onError("Error: Usuario no autenticado. Por favor, inicia sesión");
            return;
        }

        if (deviceId == null || deviceId.isEmpty()) {
            callback.onError("Error: ID de dispositivo no válido");
            return;
        }

        if (completedTask == null) {
            callback.onError("Error: Tarea completada no válida");
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference deviceRef = mDatabase.child("Dispositivos").child(userId).child(deviceId);
        
        // Usar timestamp como key para ordenar cronológicamente
        String taskKey = String.valueOf(System.currentTimeMillis());
        DatabaseReference completedTasksRef = deviceRef.child("tareasCompletadas").child(taskKey);

        completedTasksRef.setValue(completedTask)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        String errorMessage = translateFirebaseError(task.getException());
                        callback.onError("Error al guardar tarea completada: " + errorMessage);
                    }
                });
    }

    private String translateFirebaseError(Exception exception) {
        if (exception == null) {
            return "Error desconocido";
        }
        
        String exceptionMessage = exception.getMessage();
        if (exceptionMessage == null) {
            return "Error desconocido";
        }
        
        if (exceptionMessage.contains("network") || exceptionMessage.contains("Network")) {
            return "Error de conexión. Verifica tu internet";
        } else if (exceptionMessage.contains("permission") || exceptionMessage.contains("Permission")) {
            return "No tienes permiso para realizar esta acción";
        } else if (exceptionMessage.contains("timeout") || exceptionMessage.contains("Timeout")) {
            return "Tiempo de espera agotado. Intenta nuevamente";
        } else {
            return exceptionMessage;
        }
    }
}

