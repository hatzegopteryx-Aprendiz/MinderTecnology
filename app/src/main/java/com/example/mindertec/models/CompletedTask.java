package com.example.mindertec.models;

public class CompletedTask {
    private String tarea; // Descripción de la tarea
    private String fecha; // Fecha de finalización
    private String personal; // Nombre del técnico que completó la tarea

    public CompletedTask() {
        // Constructor vacío requerido por Firebase
    }

    public CompletedTask(String tarea, String fecha, String personal) {
        this.tarea = tarea;
        this.fecha = fecha;
        this.personal = personal;
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
}

