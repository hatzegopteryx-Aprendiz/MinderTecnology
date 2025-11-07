package com.example.mindertec.controllers;

import android.content.Context;

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
}

