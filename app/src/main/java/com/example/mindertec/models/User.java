package com.example.mindertec.models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class User {
    private String nombre;
    private String correo;
    private String fechaRegistro;

    public User() {
        // Constructor vacío requerido por Firebase
    }

    public User(String nombre, String correo) {
        this.nombre = nombre;
        this.correo = correo;
        // Generar fecha de registro automáticamente
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        this.fechaRegistro = sdf.format(new Date());
    }

    public User(String nombre, String correo, String fechaRegistro) {
        this.nombre = nombre;
        this.correo = correo;
        this.fechaRegistro = fechaRegistro;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}

