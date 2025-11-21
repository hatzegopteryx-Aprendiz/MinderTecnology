package com.example.mindertec.auth;

import android.content.Context;
import android.content.SharedPreferences;

public class session_manager_screen {

    private static final String PREF_NAME = "MinderTecSession";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_PHOTO = "userPhoto"; // <- NUEVO

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    public session_manager_screen(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Guardar datos del usuario al iniciar sesión
    public void saveUserSession(String userId, String userName, String userEmail) {
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, userName);
        editor.putString(KEY_USER_EMAIL, userEmail);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);

        // No tocar la foto aquí; se actualiza solo cuando el usuario sube imagen
        editor.apply();
    }

    // Guardar URL de la foto del usuario (NUEVO)
    public void saveUserPhoto(String url) {
        editor.putString(KEY_USER_PHOTO, url);
        editor.apply();
    }

    // Obtener URL de la foto (NUEVO)
    public String getUserPhoto() {
        return sharedPreferences.getString(KEY_USER_PHOTO, "");
    }

    // Obtener ID del usuario
    public String getUserId() {
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    // Obtener nombre del usuario
    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, "Usuario");
    }

    // Obtener email del usuario
    public String getUserEmail() {
        return sharedPreferences.getString(KEY_USER_EMAIL, "");
    }

    // Verificar si el usuario está logueado
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // Cerrar sesión
    public void logout() {
        editor.clear();
        editor.apply();
    }
}
