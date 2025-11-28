package com.example.mindertec.controllers;

import android.content.Context;

import com.example.mindertec.models.CompletedTask;
import com.example.mindertec.models.Device;
import com.example.mindertec.repositories.DeviceRepository;

public class DeviceController {
    private DeviceRepository deviceRepository;

    public DeviceController(Context context) {
        this.deviceRepository = new DeviceRepository(context);
    }

    public interface SaveDeviceListener {
        void onSaveSuccess(String deviceId);
        void onSaveError(String errorMessage);
        void onValidationError(String field, String message);
    }

    public interface GetDeviceListener {
        void onSuccess(com.example.mindertec.models.Device device);
        void onError(String errorMessage);
    }

    public interface RegisterInterventionListener {
        void onSuccess();
        void onError(String errorMessage);
        void onValidationError(String field, String message);
    }

    public interface AddCompletedTaskListener {
        void onSuccess();
        void onError(String errorMessage);
    }

    public void saveDevice(String nombreEquipo, String marca, String descripcion, 
                          String ultimaRevision, String ultimaRevisionIso,
                          String ultimoTecnico, String ultimoTecnicoId, 
                          String ultimoTecnicoEmail, String ultimoTecnicoNombre,
                          SaveDeviceListener listener) {
        
        // Validaciones
        if (nombreEquipo == null || nombreEquipo.trim().isEmpty()) {
            listener.onValidationError("nombre", "El nombre del equipo es requerido");
            return;
        }

        if (marca == null || marca.trim().isEmpty()) {
            listener.onValidationError("marca", "La marca es requerida");
            return;
        }

        if (descripcion == null || descripcion.trim().isEmpty()) {
            listener.onValidationError("descripcion", "La descripción es requerida");
            return;
        }

        // Crear objeto Device
        Device device = new Device(nombreEquipo, marca, descripcion, ultimaRevision, 
                                  ultimoTecnico, ultimoTecnicoId, ultimoTecnicoEmail, 
                                  ultimoTecnicoNombre);
        
        device.setUltimaRevisionIso(ultimaRevisionIso);

        // Guardar dispositivo
        deviceRepository.saveDevice(device, new DeviceRepository.SaveDeviceCallback() {
            @Override
            public void onSuccess(String deviceId) {
                listener.onSaveSuccess(deviceId);
            }

            @Override
            public void onError(String errorMessage) {
                listener.onSaveError(errorMessage);
            }
        });
    }

    public void getDeviceDetails(String deviceId, GetDeviceListener listener) {
        if (deviceId == null || deviceId.trim().isEmpty()) {
            listener.onError("ID de dispositivo no válido");
            return;
        }

        deviceRepository.getDevice(deviceId, new DeviceRepository.GetDeviceCallback() {
            @Override
            public void onSuccess(com.example.mindertec.models.Device device) {
                listener.onSuccess(device);
            }

            @Override
            public void onError(String errorMessage) {
                listener.onError(errorMessage);
            }
        });
    }

    public com.google.firebase.database.ValueEventListener getDeviceDetailsListener(String deviceId, GetDeviceListener listener) {
        if (deviceId == null || deviceId.trim().isEmpty()) {
            listener.onError("ID de dispositivo no válido");
            return null;
        }

        return deviceRepository.getDeviceListener(deviceId, new DeviceRepository.GetDeviceCallback() {
            @Override
            public void onSuccess(com.example.mindertec.models.Device device) {
                listener.onSuccess(device);
            }

            @Override
            public void onError(String errorMessage) {
                listener.onError(errorMessage);
            }
        });
    }

    public void removeDeviceListener(String deviceId, com.google.firebase.database.ValueEventListener listener) {
        deviceRepository.removeDeviceListener(deviceId, listener);
    }

    public void registerIntervention(String deviceId, String comentario, String fecha, String fechaIso,
                                     String tecnicoId, String tecnicoEmail, String tecnicoNombre,
                                     RegisterInterventionListener listener) {
        // Validaciones
        if (comentario == null || comentario.trim().isEmpty()) {
            listener.onValidationError("comentario", "El comentario es requerido");
            return;
        }

        if (deviceId == null || deviceId.trim().isEmpty()) {
            listener.onError("ID de dispositivo no válido");
            return;
        }

        // Registrar intervención usando el repositorio
        deviceRepository.updateIntervention(deviceId, comentario, fecha, fechaIso,
                tecnicoId, tecnicoEmail, tecnicoNombre,
                new DeviceRepository.UpdateInterventionCallback() {
                    @Override
                    public void onSuccess() {
                        listener.onSuccess();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        listener.onError(errorMessage);
                    }
                });
    }

    public void addCompletedTask(String deviceId, String tarea, String fecha, String personal, String ubicacion,
                                 AddCompletedTaskListener listener) {
        if (deviceId == null || deviceId.trim().isEmpty()) {
            listener.onError("ID de dispositivo no válido");
            return;
        }

        if (tarea == null || tarea.trim().isEmpty()) {
            listener.onError("La descripción de la tarea es requerida");
            return;
        }

        if (fecha == null || fecha.trim().isEmpty()) {
            listener.onError("La fecha es requerida");
            return;
        }

        if (personal == null || personal.trim().isEmpty()) {
            listener.onError("El nombre del personal es requerido");
            return;
        }

        // Ubicación puede ser null o vacía (si no se pudo obtener)
        String finalUbicacion = (ubicacion != null && !ubicacion.trim().isEmpty()) ? ubicacion : "Ubicación no disponible";

        CompletedTask completedTask = new CompletedTask(tarea, fecha, personal, finalUbicacion);

        deviceRepository.addCompletedTask(deviceId, completedTask, 
                new DeviceRepository.AddCompletedTaskCallback() {
                    @Override
                    public void onSuccess() {
                        listener.onSuccess();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        listener.onError(errorMessage);
                    }
                });
    }
}

