package com.example.mindertec.models;

public class CompletedTask {
    private String tarea; // Descripción de la tarea
    private String fecha; // Fecha de finalización
    private String personal; // Nombre del técnico que completó la tarea
    private String ubicacion; // Ubicación donde se realizó el mantenimiento

    public CompletedTask() {
        // Constructor vacío requerido por Firebase
    }

    public CompletedTask(String tarea, String fecha, String personal, String ubicacion) {
        this.tarea = tarea;
        this.fecha = fecha;
        this.personal = personal;
        this.ubicacion = ubicacion;
    }

    // Getters y Setters
    public String getTarea() {
        return tarea;
    }

    public void setTarea(String tarea) {
        this.tarea = tarea;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getPersonal() {
        return personal;
    }

    public void setPersonal(String personal) {
        this.personal = personal;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
}

