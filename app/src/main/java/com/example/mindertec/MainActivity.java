package com.example.mindertec;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mindertec.auth.login_screen;
import com.example.mindertec.auth.session_manager_screen;
import com.example.mindertec.menu.menu_screen;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private session_manager_screen sessionManager;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        
        // Inicializar SessionManager y Firebase Auth
        sessionManager = new session_manager_screen(this);
        mAuth = FirebaseAuth.getInstance();
        
        // Verificar si hay una sesión activa
        if (sessionManager.isLoggedIn() && mAuth.getCurrentUser() != null) {
            // Si hay sesión activa, ir directamente al menú
            Intent menuIntent = new Intent(this, menu_screen.class);
            startActivity(menuIntent);
        } else {
            // Si no hay sesión, ir al login
            Intent loginIntent = new Intent(this, login_screen.class);
            startActivity(loginIntent);
        }
        finish();
    }
}