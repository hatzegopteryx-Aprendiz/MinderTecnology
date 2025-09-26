package com.example.mindertec.devices;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mindertec.R;
import com.example.mindertec.menu.main_menu;

public class task_screen extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);
        Button btn_IniciarSesion = findViewById(R.id.btn_volver_m);
        btn_IniciarSesion.setOnClickListener(v ->startActivity(new Intent(this, main_menu.class)));


    }
}
