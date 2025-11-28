package com.example.mindertec.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mindertec.R;
import com.example.mindertec.controllers.AuthController;
import com.example.mindertec.menu.menu_screen;
import com.example.mindertec.utils.LightSensorHelper;
import com.example.mindertec.utils.ThemeHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class login_screen  extends AppCompatActivity {

    private TextInputEditText txt_Email, txt_Contrasena;
    private MaterialButton btn_IniciarSesion, btn_CrearCuenta;

    private String correo,contrasena;

    private AuthController authController;
    private LightSensorHelper lightSensorHelper;
    private View rootView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);
        
        rootView = findViewById(android.R.id.content);

        // Inicializar controlador MVC
        authController = new AuthController(this);
        
        // Inicializar sensor de luz
        initializeLightSensor();

        txt_Email = findViewById(R.id.txt_Email);
        txt_Contrasena = findViewById(R.id.txt_Email2);
        btn_IniciarSesion = findViewById(R.id.btn_IniciarSesion);
        btn_CrearCuenta = findViewById(R.id.btn_Registrar);

        btn_IniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                correo = txt_Email.getText().toString();
                contrasena = txt_Contrasena.getText().toString();

                loginUser();
            }
        });

        btn_CrearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login_screen.this, register_screen.class);
                startActivity(intent);
            }
        });

    }

    private void loginUser(){
        authController.login(correo, contrasena, new AuthController.LoginListener() {
            @Override
            public void onLoginSuccess(com.example.mindertec.models.User user) {
                // Mostrar mensaje de éxito
                Toast.makeText(login_screen.this, 
                        "Sesión iniciada correctamente", 
                        Toast.LENGTH_SHORT).show();
                
                // Ir al menú principal inmediatamente
                startActivity(new Intent(login_screen.this, menu_screen.class));
                finish();
            }

            @Override
            public void onLoginError(String errorMessage) {
                Toast.makeText(login_screen.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void initializeLightSensor() {
        lightSensorHelper = new LightSensorHelper(this);
        
        if (lightSensorHelper.isSensorAvailable()) {
            lightSensorHelper.setLightChangeListener(isDarkMode -> {
                runOnUiThread(() -> {
                    // Cambiar solo el fondo según la luz
                    if (rootView != null) {
                        ThemeHelper.changeBackgroundOnly(rootView, isDarkMode);
                    }
                });
            });
            lightSensorHelper.startListening();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (lightSensorHelper != null && lightSensorHelper.isSensorAvailable()) {
            lightSensorHelper.startListening();
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (lightSensorHelper != null) {
            lightSensorHelper.stopListening();
        }
    }

}

