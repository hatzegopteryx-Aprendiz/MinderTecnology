package com.example.mindertec.devices;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mindertec.R;
import com.example.mindertec.auth.session_manager_screen;
import com.example.mindertec.controllers.DeviceController;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class gestion_screen extends AppCompatActivity {

    private TextInputEditText etNombreEquipo, etMarca, etDescripcion, etUltimaRevision, etUltimoTecnico;
    private Button btnGuardar, btnCancelar;
    
    private FirebaseAuth mAuth;
    private session_manager_screen sessionManager;
    private DeviceController deviceController;
    private Calendar selectedDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.devices);

        // Inicializar Firebase y SessionManager
        mAuth = FirebaseAuth.getInstance();
        sessionManager = new session_manager_screen(this);
        deviceController = new DeviceController(this);

        // Obtener referencias a los campos
        etNombreEquipo = findViewById(R.id.etNombreEquipo);
        etMarca = findViewById(R.id.etMarca);
        etDescripcion = findViewById(R.id.etDescripcion);
        etUltimaRevision = findViewById(R.id.etUltimaRevision);
        etUltimoTecnico = findViewById(R.id.etUltimoTecnico);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);

        // Inicializar fecha seleccionada con la fecha actual
        selectedDate = Calendar.getInstance();
        updateDateField();

        etUltimaRevision.setOnClickListener(v -> showDatePicker());
        etUltimaRevision.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showDatePicker();
            }
        });

        // Prefijar la última intervención con el usuario actual
        String currentUserName = sessionManager.getUserName();
        if (currentUserName == null || currentUserName.trim().isEmpty()) {
            currentUserName = mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getEmail() != null
                    ? mAuth.getCurrentUser().getEmail()
                    : "Usuario";
        }
        etUltimoTecnico.setText(currentUserName);

        // Botón Cancelar
        btnCancelar.setOnClickListener(v -> {
            finish(); // Cerrar esta pantalla y volver a la anterior
        });

        // Botón Guardar
        btnGuardar.setOnClickListener(v -> {
            guardarDispositivo();
        });
    }

    private void showDatePicker() {
        int year = selectedDate.get(Calendar.YEAR);
        int month = selectedDate.get(Calendar.MONTH);
        int day = selectedDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            selectedDate.set(Calendar.YEAR, selectedYear);
            selectedDate.set(Calendar.MONTH, selectedMonth);
            selectedDate.set(Calendar.DAY_OF_MONTH, selectedDay);
            updateDateField();
        }, year, month, day);

        datePickerDialog.show();
    }

    private void updateDateField() {
        etUltimaRevision.setText(formatDateDisplay(selectedDate));
    }

    private String formatDateDisplay(Calendar calendar) {
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return displayFormat.format(calendar.getTime());
    }

    private void guardarDispositivo() {
        // Obtener valores de los campos
        String nombreEquipo = etNombreEquipo.getText().toString().trim();
        String marca = etMarca.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String ultimoTecnico = etUltimoTecnico.getText() != null ? etUltimoTecnico.getText().toString().trim() : "";
        String ultimaRevision = formatDateDisplay(selectedDate);
        String ultimaRevisionIso = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.getTime());

        // Obtener datos del usuario
        String userId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        String userEmail = sessionManager.getUserEmail();
        if (userEmail == null || userEmail.trim().isEmpty()) {
            userEmail = mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getEmail() != null
                    ? mAuth.getCurrentUser().getEmail()
                    : "";
        }
        String userName = sessionManager.getUserName();
        if (userName == null || userName.trim().isEmpty()) {
            userName = userEmail != null && !userEmail.isEmpty() ? userEmail : "Usuario";
        }

        if (ultimoTecnico.isEmpty()) {
            ultimoTecnico = userName;
        }

        final String finalUserName = userName;

        // Usar el controlador para guardar el dispositivo
        deviceController.saveDevice(nombreEquipo, marca, descripcion, ultimaRevision, ultimaRevisionIso,
                ultimoTecnico, userId, userEmail, userName, new DeviceController.SaveDeviceListener() {
                    @Override
                    public void onSaveSuccess(String deviceId) {
                        Toast.makeText(gestion_screen.this, 
                                "Dispositivo guardado correctamente", 
                                Toast.LENGTH_SHORT).show();
                        
                        // Limpiar campos
                        etNombreEquipo.setText("");
                        etMarca.setText("");
                        etDescripcion.setText("");
                        selectedDate = Calendar.getInstance();
                        updateDateField();
                        etUltimoTecnico.setText(finalUserName);
                        
                        // Volver a la pantalla anterior
                        finish();
                    }

                    @Override
                    public void onSaveError(String errorMessage) {
                        Toast.makeText(gestion_screen.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onValidationError(String field, String message) {
                        switch (field) {
                            case "nombre":
                                etNombreEquipo.setError(message);
                                etNombreEquipo.requestFocus();
                                break;
                            case "marca":
                                etMarca.setError(message);
                                etMarca.requestFocus();
                                break;
                            case "descripcion":
                                etDescripcion.setError(message);
                                etDescripcion.requestFocus();
                                break;
                        }
                    }
                });
    }
}
