package com.example.mindertec.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mindertec.R;
import com.example.mindertec.controllers.AuthController;

public class register_screen extends AppCompatActivity {

    private EditText edText_Nombre, edText_Correo, edText_Contrasena;

    private Button btn_Registrar;

    private String nombre, correo, contrasena;

    private AuthController authController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.register);

        // Inicializar controlador MVC
        authController = new AuthController(this);

        edText_Nombre = findViewById(R.id.edText_Nombre);
        edText_Correo = findViewById(R.id.edText_Correo);
        edText_Contrasena = findViewById(R.id.edText_Contrasena);
        btn_Registrar = findViewById(R.id.btn_Registrar);

        btn_Registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nombre =  edText_Nombre.getText().toString();
                correo =  edText_Correo.getText().toString();
                contrasena =  edText_Contrasena.getText().toString();

                registrarUsuario();
            }
        });
        Button btn_Volver = findViewById(R.id.btn_Volver);
        btn_Volver.setOnClickListener(v ->startActivity(new Intent(this, login_screen.class)));

    }

    private void registrarUsuario(){
        authController.register(nombre, correo, contrasena, new AuthController.RegisterListener() {
            @Override
            public void onRegisterSuccess(com.example.mindertec.models.User user) {
                Toast.makeText(register_screen.this, 
                        "Cuenta creada correctamente", 
                        Toast.LENGTH_SHORT).show();
                
                startActivity(new Intent(register_screen.this,login_screen.class));
                finish();
            }

            @Override
            public void onRegisterError(String errorMessage) {
                Toast.makeText(register_screen.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}