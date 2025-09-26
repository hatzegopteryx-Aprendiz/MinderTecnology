package com.example.mindertec.devices;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mindertec.R;
import com.example.mindertec.menu.menu_screen;

public class devices_screen extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team);
        Button btn_managet = findViewById(R.id.btn_gestion);
        Button volver= findViewById(R.id.btn_gestion2);
        volver.setOnClickListener(v -> startActivity(new Intent (this, menu_screen.class)));
        btn_managet.setOnClickListener(v ->startActivity(new Intent(this, gestion_screen.class)));




    }
}
