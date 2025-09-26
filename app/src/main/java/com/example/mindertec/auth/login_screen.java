package com.example.mindertec.auth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mindertec.R;

public class login_screen  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);

        Button btn_IniciarSesion = findViewById(R.id.btn_IniciarSesion);

        btn_IniciarSesion.setOnClickListener(v ->startActivity(new Intent(this, main_menu.class)));

    }
}

