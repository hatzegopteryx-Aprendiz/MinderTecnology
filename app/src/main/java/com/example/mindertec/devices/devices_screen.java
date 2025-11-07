package com.example.mindertec.devices;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mindertec.R;
import com.example.mindertec.menu.menu_screen;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class devices_screen extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private TextView tvIdEquipo, tvDescripcion, tvUltimaRevision;
    private ImageView imgEquipo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team);

        mDatabase = FirebaseDatabase.getInstance().getReference("Dispositivos");

        tvIdEquipo = findViewById(R.id.tvIdEquipo);
        tvDescripcion = findViewById(R.id.tvDescripcion);
        tvUltimaRevision = findViewById(R.id.tvUltimaRevision);
        imgEquipo = findViewById(R.id.imgEquipo);

        Button btn_Agregar = findViewById(R.id.btn_Agregar);
        Button volver = findViewById(R.id.btn_gestion2);

        volver.setOnClickListener(v -> startActivity(new Intent(this, menu_screen.class)));
        btn_Agregar.setOnClickListener(v -> startActivity(new Intent(this, gestion_screen.class)));

        // 🔹 Leer los datos desde la BD
        loadDevices();
    }

    private void loadDevices() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(devices_screen.this, "No hay dispositivos registrados", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Solo mostrar el primero como ejemplo (puedes luego hacerlo con lista)
                for (DataSnapshot deviceSnap : snapshot.getChildren()) {
                    // Si tus datos están dentro de un UID de usuario, toma su primer hijo
                    for (DataSnapshot innerSnap : deviceSnap.getChildren()) {
                        String id = innerSnap.child("deviceId").getValue(String.class);
                        String nombre = innerSnap.child("nombre").getValue(String.class);
                        String marca = innerSnap.child("marca").getValue(String.class);
                        String descripcion = innerSnap.child("descripcion").getValue(String.class);
                        String ultimaRevision = innerSnap.child("ultimaRevision").getValue(String.class);

                        // Mostrar en tu tarjeta
                        tvIdEquipo.setText("ID: " + (id != null ? id : "sin id") + " - " + nombre + " (" + marca + ")");
                        tvDescripcion.setText("Descripción: " + descripcion);
                        tvUltimaRevision.setText("Última revisión: " + ultimaRevision);
                        break; // Muestra solo el primero (luego haremos lista)
                    }
                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(devices_screen.this, "Error al leer datos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
