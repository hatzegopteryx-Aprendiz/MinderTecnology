package com.example.mindertec.controllers;

import android.content.Context;

import com.example.mindertec.models.User;
import com.example.mindertec.repositories.UserRepository;

public class AuthController {
    private UserRepository userRepository;

    public AuthController(Context context) {
        this.userRepository = new UserRepository(context);
    }

    public interface LoginListener {
        void onLoginSuccess(User user);
        void onLoginError(String errorMessage);
    }

    public interface RegisterListener {
        void onRegisterSuccess(User user);
        void onRegisterError(String errorMessage);
    }

    public void login(String correo, String contrasena, LoginListener listener) {
        if (correo == null || correo.trim().isEmpty()) {
            listener.onLoginError("Complete los campos");
            return;
        }

        if (contrasena == null || contrasena.trim().isEmpty()) {
            listener.onLoginError("Complete los campos");
            return;
        }

        userRepository.loginUser(correo, contrasena, new UserRepository.LoginCallback() {
            @Override
            public void onSuccess(User user) {
                listener.onLoginSuccess(user);
            }

            @Override
            public void onError(String errorMessage) {
                listener.onLoginError(errorMessage);
            }
        });
    }

    public void register(String nombre, String correo, String contrasena, RegisterListener listener) {
        // Validaciones
        if (nombre == null || nombre.trim().isEmpty()) {
            listener.onRegisterError("Debe completar los campos");
            return;
        }

        if (correo == null || correo.trim().isEmpty()) {
            listener.onRegisterError("Debe completar los campos");
            return;
        }

        if (contrasena == null || contrasena.trim().isEmpty()) {
            listener.onRegisterError("Debe completar los campos");
            return;
        }

        if (contrasena.length() < 6) {
            listener.onRegisterError("La contraseña debe tener minimo 6 caracteres.");
            return;
        }

        userRepository.registerUser(nombre, correo, contrasena, new UserRepository.RegisterCallback() {
            @Override
            public void onSuccess(User user) {
                listener.onRegisterSuccess(user);
            }

            @Override
            public void onError(String errorMessage) {
                listener.onRegisterError(errorMessage);
            }
        });
    }
}

