package com.example.mindertec.devices;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mindertec.R;
import com.example.mindertec.menu.menu_screen;
import com.example.mindertec.models.Device;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class devices_screen extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DeviceAdapter adapter;
    private List<Device> deviceList;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team);

        recyclerView = findViewById(R.id.recyclerDevices);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        deviceList = new ArrayList<>();

        adapter = new DeviceAdapter(deviceList);
        recyclerView.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Dispositivos");

        Button btn_Agregar = findViewById(R.id.btn_Agregar);
        Button volver = findViewById(R.id.btn_gestion2);

        volver.setOnClickListener(v -> startActivity(new Intent(this, menu_screen.class)));
        btn_Agregar.setOnClickListener(v -> startActivity(new Intent(this, gestion_screen.class)));

        loadDevicesForUser();
    }

    private void loadDevicesForUser() {
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        if (userId == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        mDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                deviceList.clear();
                for (DataSnapshot deviceSnap : snapshot.getChildren()) {
                    Device device = deviceSnap.getValue(Device.class);
                    if (device != null) {
                        deviceList.add(device);
                    }
                }
                adapter.notifyDataSetChanged();

                if (deviceList.isEmpty()) {
                    Toast.makeText(devices_screen.this, "No hay dispositivos registrados", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(devices_screen.this, "Error al leer datos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
