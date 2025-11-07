package com.example.mindertec.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Device {
    private String deviceId;
    private String nombre;
    private String marca;
    private String descripcion;
    private String ultimaRevision;
    private String ultimaRevisionIso;
    private String ultimaIntervencion;
    private String ultimaIntervencionId;
    private String ultimaIntervencionEmail;
    private String ultimaIntervencionNombre;
    private String fechaRegistro;
    private String ultimaIntervencionFecha;

    public Device() {
        // Constructor vacío requerido por Firebase
    }

    public Device(String nombre, String marca, String descripcion, String ultimaRevision, 
                  String ultimaIntervencion, String ultimaIntervencionId, 
                  String ultimaIntervencionEmail, String ultimaIntervencionNombre) {
        this.nombre = nombre;
        this.marca = marca;
        this.descripcion = descripcion;
        this.ultimaRevision = ultimaRevision;
        this.ultimaIntervencion = ultimaIntervencion;
        this.ultimaIntervencionId = ultimaIntervencionId;
        this.ultimaIntervencionEmail = ultimaIntervencionEmail;
        this.ultimaIntervencionNombre = ultimaIntervencionNombre;
        
        // Generar fechas automáticamente
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        this.fechaRegistro = isoFormat.format(new Date());
        this.ultimaIntervencionFecha = this.fechaRegistro;
        
        // Convertir fecha de revisión a ISO si es necesario
        try {
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = displayFormat.parse(ultimaRevision);
            this.ultimaRevisionIso = isoFormat.format(date);
        } catch (Exception e) {
            this.ultimaRevisionIso = ultimaRevision;
        }
    }

    // Getters y Setters
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUltimaRevision() {
        return ultimaRevision;
    }

    public void setUltimaRevision(String ultimaRevision) {
        this.ultimaRevision = ultimaRevision;
    }

    public String getUltimaRevisionIso() {
        return ultimaRevisionIso;
    }

    public void setUltimaRevisionIso(String ultimaRevisionIso) {
        this.ultimaRevisionIso = ultimaRevisionIso;
    }

    public String getUltimaIntervencion() {
        return ultimaIntervencion;
    }

    public void setUltimaIntervencion(String ultimaIntervencion) {
        this.ultimaIntervencion = ultimaIntervencion;
    }

    public String getUltimaIntervencionId() {
        return ultimaIntervencionId;
    }

    public void setUltimaIntervencionId(String ultimaIntervencionId) {
        this.ultimaIntervencionId = ultimaIntervencionId;
    }

    public String getUltimaIntervencionEmail() {
        return ultimaIntervencionEmail;
    }

    public void setUltimaIntervencionEmail(String ultimaIntervencionEmail) {
        this.ultimaIntervencionEmail = ultimaIntervencionEmail;
    }

    public String getUltimaIntervencionNombre() {
        return ultimaIntervencionNombre;
    }

    public void setUltimaIntervencionNombre(String ultimaIntervencionNombre) {
        this.ultimaIntervencionNombre = ultimaIntervencionNombre;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getUltimaIntervencionFecha() {
        return ultimaIntervencionFecha;
    }

    public void setUltimaIntervencionFecha(String ultimaIntervencionFecha) {
        this.ultimaIntervencionFecha = ultimaIntervencionFecha;
    }
}

