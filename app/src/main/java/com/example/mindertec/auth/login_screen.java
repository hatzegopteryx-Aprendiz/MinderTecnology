package com.example.mindertec.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mindertec.R;
import com.example.mindertec.menu.menu_screen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class login_screen  extends AppCompatActivity {

    private EditText txt_Email, txt_Contrasena;
    private Button btn_IniciarSesion;

    private String correo,contrasena;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);

        mAuth = FirebaseAuth.getInstance();

        txt_Email = findViewById(R.id.txt_Email);
        txt_Contrasena = findViewById(R.id.txt_Email2);
        btn_IniciarSesion = findViewById(R.id.btn_IniciarSesion);

        btn_IniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                correo = txt_Email.getText().toString();
                contrasena = txt_Contrasena.getText().toString();

                if(!correo.isEmpty() && !contrasena.isEmpty()) {
                    loginUser();

                }else {
                    Toast.makeText(login_screen.this,"Complete los campos",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void loginUser(){
        mAuth.signInWithEmailAndPassword(correo,contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    startActivity(new Intent(login_screen.this,menu_screen.class));
                    finish();
                }else {
                    Toast.makeText(login_screen.this,"Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

}

