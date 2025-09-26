package com.example.mindertec.menu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mindertec.R;
import com.example.mindertec.auth.login_screen;
import com.example.mindertec.devices.devices_screen;
import com.example.mindertec.devices.task_screen;
import com.example.mindertec.profile.profile_screen;

public class menu_screen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        Button btn_Dispositivos = findViewById(R.id.btn_Dispositivos);
        btn_Dispositivos.setOnClickListener(v ->startActivity(new Intent(this, devices_screen.class)));

        Button btn_Perfil = findViewById(R.id.btn_Perfil);
        btn_Perfil.setOnClickListener(v ->startActivity(new Intent(this, profile_screen.class)));

        Button btn_Tareas = findViewById(R.id.btn_Tareas);
        btn_Tareas.setOnClickListener(v ->startActivity(new Intent(this, task_screen.class)));

        Button btn_Volver = findViewById(R.id.btn_VolverMenu);
        btn_Volver.setOnClickListener(v ->startActivity(new Intent(this, login_screen.class)));
    }
}