package com.example.mindertec.devices;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mindertec.R;
import com.example.mindertec.menu.menu_screen;
import com.example.mindertec.models.Device;
import com.example.mindertec.utils.LightSensorHelper;
import com.example.mindertec.utils.ThemeHelper;
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
    private ValueEventListener devicesListener;
    private LightSensorHelper lightSensorHelper;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team);
        
        rootView = findViewById(android.R.id.content);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Dispositivos");
        
        // Inicializar sensor de luz
        initializeLightSensor();

        // Verificar autenticación
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inicializar RecyclerView
        recyclerView = findViewById(R.id.recyclerDevices);
        if (recyclerView == null) {
            Toast.makeText(this, "Error: RecyclerView no encontrado en el layout", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        deviceList = new ArrayList<>();
        adapter = new DeviceAdapter(deviceList);
        recyclerView.setAdapter(adapter);

        // Botones
        Button btn_Agregar = findViewById(R.id.btn_Agregar);
        Button volver = findViewById(R.id.btn_gestion2);

        if (btn_Agregar != null) {
            btn_Agregar.setOnClickListener(v -> startActivity(new Intent(this, gestion_screen.class)));
        }

        if (volver != null) {
            volver.setOnClickListener(v -> {
                finish(); // Cerrar esta pantalla y volver al menú
            });
        }

        // Cargar dispositivos del usuario actual
        loadDevicesForUser();
    }

    private void loadDevicesForUser() {
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;

        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Referencia específica a los dispositivos del usuario actual
        // Estructura: Dispositivos/{userId}/{deviceId}
        DatabaseReference userDevicesRef = mDatabase.child(userId);

        devicesListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    deviceList.clear();
                    
                    if (snapshot.exists() && snapshot.hasChildren()) {
                        for (DataSnapshot deviceSnap : snapshot.getChildren()) {
                            try {
                                Device device = deviceSnap.getValue(Device.class);
                                if (device != null) {
                                    // Asegurar que el deviceId esté establecido
                                    if (device.getDeviceId() == null || device.getDeviceId().isEmpty()) {
                                        String key = deviceSnap.getKey();
                                        if (key != null) {
                                            device.setDeviceId(key);
                                        }
                                    }
                                    deviceList.add(device);
                                }
                            } catch (Exception e) {
                                // Si hay un error al deserializar un dispositivo, continuar con los demás
                                e.printStackTrace();
                                continue;
                            }
                        }
                    }
                    
                    // Notificar cambios en el adapter
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }

                    if (deviceList.isEmpty()) {
                        Toast.makeText(devices_screen.this, "No hay dispositivos registrados", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(devices_screen.this, "Error al cargar dispositivos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(devices_screen.this, 
                        "Error al leer datos: " + error.getMessage(), 
                        Toast.LENGTH_SHORT).show();
            }
        };

        userDevicesRef.addValueEventListener(devicesListener);
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
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remover el listener para evitar memory leaks
        if (devicesListener != null && mDatabase != null && mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            if (userId != null) {
                mDatabase.child(userId).removeEventListener(devicesListener);
            }
        }
        if (lightSensorHelper != null) {
            lightSensorHelper.stopListening();
        }
    }
}
