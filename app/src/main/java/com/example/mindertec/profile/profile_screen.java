package com.example.mindertec.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mindertec.R;
import com.example.mindertec.auth.session_manager_screen;
import com.example.mindertec.menu.menu_screen;

public class profile_screen extends AppCompatActivity {
    
    private session_manager_screen sessionManager;
    private TextView tvNombre,tvCorreo;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        sessionManager = new session_manager_screen(this);
        tvNombre = findViewById(R.id.tvNombre);
        tvCorreo = findViewById(R.id.tvCorreo);
        loadUserData();
        
        // Configurar botón volver
        Button btn_back = findViewById(R.id.btn_volver_prf);
        btn_back.setOnClickListener(v -> startActivity(new Intent(this, menu_screen.class)));
    }
    
    private void loadUserData() {
        // Obtener datos del usuario desde SessionManager
        String userName = sessionManager.getUserName();
        String userEmail = sessionManager.getUserEmail();
        
        // Actualizar los TextViews
        if (tvNombre != null) {
            tvNombre.setText(userName);
        }
        
        if (tvCorreo != null) {
            tvCorreo.setText(userEmail);
        }
    }
}
