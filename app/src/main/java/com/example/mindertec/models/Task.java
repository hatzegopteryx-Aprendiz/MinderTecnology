package com.example.mindertec.models;

public class Task {
    private String taskId;
    private String deviceId;
    private String deviceName;
    private String descripcion;
    private String fechaInicio;
    private String fechaInicioIso;
    private String fechaTerminoEstimada;
    private String fechaTerminoEstimadaIso;
    private String tecnicoId;
    private String tecnicoEmail;
    private String tecnicoNombre;
    private String estado; // "pendiente", "en_proceso", "completada"
    private String fechaCreacion;

    public Task() {
        // Constructor vacío requerido por Firebase
    }

    public Task(String deviceId, String deviceName, String descripcion, 
                String fechaInicio, String fechaInicioIso,
                String fechaTerminoEstimada, String fechaTerminoEstimadaIso,
                String tecnicoId, String tecnicoEmail, String tecnicoNombre) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaInicioIso = fechaInicioIso;
        this.fechaTerminoEstimada = fechaTerminoEstimada;
        this.fechaTerminoEstimadaIso = fechaTerminoEstimadaIso;
        this.tecnicoId = tecnicoId;
        this.tecnicoEmail = tecnicoEmail;
        this.tecnicoNombre = tecnicoNombre;
        this.estado = "pendiente";
        
        // Fecha de creación en formato ISO
        java.text.SimpleDateFormat isoFormat = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        this.fechaCreacion = isoFormat.format(new java.util.Date());
    }

    // Getters y Setters
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaInicioIso() {
        return fechaInicioIso;
    }

    public void setFechaInicioIso(String fechaInicioIso) {
        this.fechaInicioIso = fechaInicioIso;
    }

    public String getFechaTerminoEstimada() {
        return fechaTerminoEstimada;
    }

    public void setFechaTerminoEstimada(String fechaTerminoEstimada) {
        this.fechaTerminoEstimada = fechaTerminoEstimada;
    }

    public String getFechaTerminoEstimadaIso() {
        return fechaTerminoEstimadaIso;
    }

    public void setFechaTerminoEstimadaIso(String fechaTerminoEstimadaIso) {
        this.fechaTerminoEstimadaIso = fechaTerminoEstimadaIso;
    }

    public String getTecnicoId() {
        return tecnicoId;
    }

    public void setTecnicoId(String tecnicoId) {
        this.tecnicoId = tecnicoId;
    }

    public String getTecnicoEmail() {
        return tecnicoEmail;
    }

    public void setTecnicoEmail(String tecnicoEmail) {
        this.tecnicoEmail = tecnicoEmail;
    }

    public String getTecnicoNombre() {
        return tecnicoNombre;
    }

    public void setTecnicoNombre(String tecnicoNombre) {
        this.tecnicoNombre = tecnicoNombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}

