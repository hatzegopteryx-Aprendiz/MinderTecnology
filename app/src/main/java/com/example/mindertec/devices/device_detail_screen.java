package com.example.mindertec.devices;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mindertec.R;
import com.example.mindertec.auth.session_manager_screen;
import com.example.mindertec.controllers.DeviceController;
import com.example.mindertec.controllers.TaskController;
import com.example.mindertec.models.CompletedTask;
import com.example.mindertec.models.Device;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class device_detail_screen extends AppCompatActivity {

    private TextView tvNombre, tvMarca, tvDescripcion, tvUltimaRevision, 
                     tvUltimoTecnico, tvFechaRegistro, tvUltimaIntervencionFecha;
    
    private DeviceController deviceController;
    private TaskController taskController;
    private FirebaseAuth mAuth;
    private session_manager_screen sessionManager;
    private String deviceId;
    private String deviceName;
    private ValueEventListener deviceListener;
    private ValueEventListener completedTasksListener;
    private Calendar selectedTerminoDate;
    private android.widget.ListView listCompletedTasks;
    private CompletedTaskAdapter completedTaskAdapter;
    private List<CompletedTask> completedTaskList;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_detail);

        // Inicializar Firebase Auth y Controllers MVC
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        deviceController = new DeviceController(this);
        taskController = new TaskController(this);
        taskController.setDeviceController(deviceController);
        sessionManager = new session_manager_screen(this);
        selectedTerminoDate = Calendar.getInstance();
        completedTaskList = new ArrayList<>();
        completedTaskAdapter = new CompletedTaskAdapter(completedTaskList);

        // Verificar autenticación
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Obtener deviceId del Intent
        deviceId = getIntent().getStringExtra("DEVICE_ID");
        if (deviceId == null || deviceId.isEmpty()) {
            Toast.makeText(this, "Error: ID de dispositivo no válido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupBackButton();
        setupIntervenirButton();
        loadDeviceData();
        loadCompletedTasks();
    }

    private void initializeViews() {
        tvNombre = findViewById(R.id.tvNombre);
        tvMarca = findViewById(R.id.tvMarca);
        tvDescripcion = findViewById(R.id.tvDescripcion);
        tvUltimaRevision = findViewById(R.id.tvUltimaRevision);
        tvUltimoTecnico = findViewById(R.id.tvUltimoTecnico);
        tvFechaRegistro = findViewById(R.id.tvFechaRegistro);
        tvUltimaIntervencionFecha = findViewById(R.id.tvUltimaIntervencionFecha);
        listCompletedTasks = findViewById(R.id.listCompletedTasks);
        
        if (listCompletedTasks != null) {
            listCompletedTasks.setAdapter(completedTaskAdapter);
            
            // Configurar click listener directamente en el ListView
            listCompletedTasks.setOnItemClickListener((parent, view, position, id) -> {
                try {
                    CompletedTask completedTask = (CompletedTask) completedTaskAdapter.getItem(position);
                    if (completedTask != null) {
                        android.util.Log.d("device_detail", "Click en tarea: " + completedTask.getTarea());
                        showCompletedTaskDetailDialog(completedTask);
                    } else {
                        android.util.Log.d("device_detail", "CompletedTask es null en posición: " + position);
                        Toast.makeText(device_detail_screen.this, "Error: No se pudo obtener la información de la tarea", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    android.util.Log.e("device_detail", "Error al hacer click en tarea: " + e.getMessage());
                    Toast.makeText(device_detail_screen.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void setupBackButton() {
        Button btnVolver = findViewById(R.id.btnVolver);
        if (btnVolver != null) {
            btnVolver.setOnClickListener(v -> finish());
        }
    }

    private void setupIntervenirButton() {
        MaterialButton btnIntervenir = findViewById(R.id.btnIntervenir);
        if (btnIntervenir != null) {
            btnIntervenir.setOnClickListener(v -> showInterventionDialog());
        }
    }

    private void showInterventionDialog() {
        // Crear el diálogo
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_intervencion);
        dialog.setCancelable(true);
        
        // Configurar el tamaño de la ventana del diálogo
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.9),
                    android.view.WindowManager.LayoutParams.WRAP_CONTENT
            );
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Obtener referencias a los campos del diálogo
        TextInputEditText etFechaInicio = dialog.findViewById(R.id.etFechaInicio);
        TextInputEditText etFechaTermino = dialog.findViewById(R.id.etFechaTermino);
        TextInputEditText etDescripcion = dialog.findViewById(R.id.etDescripcion);
        MaterialButton btnCancelar = dialog.findViewById(R.id.btnCancelar);
        MaterialButton btnGuardar = dialog.findViewById(R.id.btnGuardarIntervencion);

        // Establecer la fecha actual (no modificable)
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        final SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        final Calendar calendar = Calendar.getInstance();
        final String fechaActual = dateFormat.format(calendar.getTime());
        final String fechaIso = isoFormat.format(calendar.getTime());

        // Inicializar fecha de término con la fecha actual + 7 días
        selectedTerminoDate = Calendar.getInstance();
        selectedTerminoDate.add(Calendar.DAY_OF_MONTH, 7);
        final String fechaTerminoDefault = dateFormat.format(selectedTerminoDate.getTime());

        if (etFechaInicio != null) {
            etFechaInicio.setText(fechaActual);
        }

        if (etFechaTermino != null) {
            etFechaTermino.setText(fechaTerminoDefault);
            // Configurar DatePicker para la fecha de término
            etFechaTermino.setOnClickListener(v -> {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        this,
                        (view, year, month, dayOfMonth) -> {
                            selectedTerminoDate.set(Calendar.YEAR, year);
                            selectedTerminoDate.set(Calendar.MONTH, month);
                            selectedTerminoDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            String fechaSeleccionada = dateFormat.format(selectedTerminoDate.getTime());
                            etFechaTermino.setText(fechaSeleccionada);
                        },
                        selectedTerminoDate.get(Calendar.YEAR),
                        selectedTerminoDate.get(Calendar.MONTH),
                        selectedTerminoDate.get(Calendar.DAY_OF_MONTH)
                );
                // Establecer fecha mínima como hoy
                datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                datePickerDialog.show();
            });
        }

        // Botón Cancelar
        if (btnCancelar != null) {
            btnCancelar.setOnClickListener(v -> dialog.dismiss());
        }

        // Botón Guardar
        if (btnGuardar != null) {
            btnGuardar.setOnClickListener(v -> {
                final String descripcion = etDescripcion != null ? etDescripcion.getText().toString().trim() : "";
                final String fechaTermino = etFechaTermino != null ? etFechaTermino.getText().toString().trim() : "";

                if (descripcion.isEmpty()) {
                    Toast.makeText(this, "Por favor, ingrese una descripción", Toast.LENGTH_SHORT).show();
                    if (etDescripcion != null) {
                        etDescripcion.requestFocus();
                    }
                    return;
                }

                if (fechaTermino.isEmpty()) {
                    Toast.makeText(this, "Por favor, seleccione la fecha estimada de término", Toast.LENGTH_SHORT).show();
                    if (etFechaTermino != null) {
                        etFechaTermino.requestFocus();
                    }
                    return;
                }

                // Obtener datos del usuario actual
                String userIdTemp = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
                String userEmailTemp = sessionManager.getUserEmail();
                if (userEmailTemp == null || userEmailTemp.trim().isEmpty()) {
                    userEmailTemp = mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getEmail() != null
                            ? mAuth.getCurrentUser().getEmail() : "";
                }
                String userNameTemp = sessionManager.getUserName();
                if (userNameTemp == null || userNameTemp.trim().isEmpty()) {
                    userNameTemp = userEmailTemp != null && !userEmailTemp.isEmpty() ? userEmailTemp : "Usuario";
                }

                // Crear variables finales para usar en las clases internas
                final String userId = userIdTemp;
                final String userEmail = userEmailTemp;
                final String userName = userNameTemp;

                // Convertir fecha de término a ISO
                final String fechaTerminoIso = isoFormat.format(selectedTerminoDate.getTime());

                // Registrar la intervención usando el controller MVC
                deviceController.registerIntervention(deviceId, descripcion, fechaActual, fechaIso,
                        userId, userEmail, userName,
                        new DeviceController.RegisterInterventionListener() {
                            @Override
                            public void onSuccess() {
                                // Guardar la tarea en el calendario
                                taskController.saveTask(deviceId, deviceName != null ? deviceName : "Dispositivo",
                                        descripcion, fechaActual, fechaIso,
                                        fechaTermino, fechaTerminoIso,
                                        userId, userEmail, userName,
                                        new TaskController.SaveTaskListener() {
                                            @Override
                                            public void onSaveSuccess(String taskId) {
                                                Toast.makeText(device_detail_screen.this,
                                                        "Intervención y tarea registradas exitosamente",
                                                        Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void onSaveError(String errorMessage) {
                                                Toast.makeText(device_detail_screen.this,
                                                        "Intervención registrada, pero error al crear tarea: " + errorMessage,
                                                        Toast.LENGTH_LONG).show();
                                                dialog.dismiss();
                                            }

                                            @Override
                                            public void onValidationError(String field, String message) {
                                                Toast.makeText(device_detail_screen.this, message, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }

                            @Override
                            public void onError(String errorMessage) {
                                Toast.makeText(device_detail_screen.this,
                                        errorMessage,
                                        Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onValidationError(String field, String message) {
                                if ("comentario".equals(field) && etDescripcion != null) {
                                    etDescripcion.setError(message);
                                    etDescripcion.requestFocus();
                                } else {
                                    Toast.makeText(device_detail_screen.this, message, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            });
        }

        // Mostrar el diálogo
        dialog.show();
    }

    private void loadDeviceData() {
        // Usar el controller MVC para obtener los datos del dispositivo
        deviceListener = deviceController.getDeviceDetailsListener(deviceId, new DeviceController.GetDeviceListener() {
            @Override
            public void onSuccess(Device device) {
                displayDeviceData(device);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(device_detail_screen.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayDeviceData(Device device) {
        // Guardar el nombre del dispositivo para usarlo en la tarea
        deviceName = device.getNombre() != null ? device.getNombre() : "Sin nombre";
        
        // Mostrar los datos del dispositivo en los TextViews
        if (tvNombre != null) {
            tvNombre.setText(deviceName);
        }
        
        if (tvMarca != null) {
            tvMarca.setText(device.getMarca() != null ? device.getMarca() : "Sin marca");
        }
        
        if (tvDescripcion != null) {
            tvDescripcion.setText(device.getDescripcion() != null ? device.getDescripcion() : "Sin descripción");
        }
        
        if (tvUltimaRevision != null) {
            tvUltimaRevision.setText(device.getUltimaRevision() != null ? device.getUltimaRevision() : "Sin fecha");
        }
        
        if (tvUltimoTecnico != null) {
            String tecnico = device.getUltimaIntervencionNombre() != null ? 
                    device.getUltimaIntervencionNombre() : 
                    (device.getUltimaIntervencionEmail() != null ? device.getUltimaIntervencionEmail() : "Sin información");
            tvUltimoTecnico.setText(tecnico);
        }
        
        if (tvFechaRegistro != null) {
            tvFechaRegistro.setText(device.getFechaRegistro() != null ? device.getFechaRegistro() : "Sin fecha");
        }
        
        if (tvUltimaIntervencionFecha != null) {
            tvUltimaIntervencionFecha.setText(device.getUltimaIntervencionFecha() != null ? 
                    device.getUltimaIntervencionFecha() : "Sin fecha");
        }
    }

    private void loadCompletedTasks() {
        if (mAuth.getCurrentUser() == null || deviceId == null) {
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference completedTasksRef = mDatabase.child("Dispositivos")
                .child(userId).child(deviceId).child("tareasCompletadas");

        completedTasksListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                completedTaskList.clear();
                
                if (snapshot.exists() && snapshot.hasChildren()) {
                    // Crear lista temporal para ordenar por fecha (timestamp como key)
                    List<CompletedTask> tempList = new ArrayList<>();
                    for (DataSnapshot taskSnap : snapshot.getChildren()) {
                        CompletedTask completedTask = taskSnap.getValue(CompletedTask.class);
                        if (completedTask != null) {
                            tempList.add(completedTask);
                        }
                    }
                    
                    // Ordenar por fecha (más recientes primero)
                    // Como las keys son timestamps, ya están ordenadas, pero invertimos para mostrar más recientes primero
                    Collections.reverse(tempList);
                    completedTaskList.addAll(tempList);
                }
                
                completedTaskAdapter.updateTasks(completedTaskList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Error silencioso, no mostrar toast para no molestar
            }
        };

        completedTasksRef.addValueEventListener(completedTasksListener);
    }

    private void showCompletedTaskDetailDialog(CompletedTask completedTask) {
        if (completedTask == null) {
            Toast.makeText(this, "Error: Información de tarea no disponible", Toast.LENGTH_SHORT).show();
            return;
        }
        
        final android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_detalle_tarea_completada);
        dialog.setCancelable(true);

        // Configurar ventana del diálogo
        android.view.Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
            window.setGravity(android.view.Gravity.CENTER);
        }

        android.widget.TextView tvDescripcion = dialog.findViewById(R.id.tvDescripcionCompleta);
        android.widget.TextView tvFecha = dialog.findViewById(R.id.tvFechaCompleta);
        android.widget.TextView tvUbicacion = dialog.findViewById(R.id.tvUbicacionCompleta);
        android.widget.TextView tvPersonal = dialog.findViewById(R.id.tvPersonalCompleto);
        com.google.android.material.button.MaterialButton btnCerrar = dialog.findViewById(R.id.btnCerrar);

        if (tvDescripcion != null) {
            tvDescripcion.setText(completedTask.getTarea() != null ? completedTask.getTarea() : "Sin descripción");
        }

        if (tvFecha != null) {
            tvFecha.setText(completedTask.getFecha() != null ? completedTask.getFecha() : "Sin fecha");
        }

        if (tvUbicacion != null) {
            tvUbicacion.setText(completedTask.getUbicacion() != null ? completedTask.getUbicacion() : "Ubicación no disponible");
        }

        if (tvPersonal != null) {
            tvPersonal.setText(completedTask.getPersonal() != null ? completedTask.getPersonal() : "Sin información");
        }

        if (btnCerrar != null) {
            btnCerrar.setOnClickListener(v -> dialog.dismiss());
        }

        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remover listeners para evitar memory leaks
        if (deviceListener != null && deviceId != null) {
            deviceController.removeDeviceListener(deviceId, deviceListener);
        }
        
        if (completedTasksListener != null && mDatabase != null && mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            if (userId != null && deviceId != null) {
                mDatabase.child("Dispositivos").child(userId).child(deviceId)
                        .child("tareasCompletadas").removeEventListener(completedTasksListener);
            }
        }
    }
}

