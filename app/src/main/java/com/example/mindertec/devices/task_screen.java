package com.example.mindertec.devices;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mindertec.R;
import com.example.mindertec.controllers.TaskController;
import com.example.mindertec.menu.menu_screen;
import com.example.mindertec.models.Task;

import java.util.ArrayList;
import java.util.List;

public class task_screen extends AppCompatActivity {
    
    private ListView listActividades;
    private TextView tvActividades;
    private TaskAdapter taskAdapter;
    private TaskController taskController;
    private List<Task> taskList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar);

        // Inicializar controllers MVC
        taskController = new TaskController(this);
        
        // Configurar DeviceController en TaskController para guardar historial
        com.example.mindertec.controllers.DeviceController deviceController = 
                new com.example.mindertec.controllers.DeviceController(this);
        taskController.setDeviceController(deviceController);
        
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList);

        // Inicializar vistas
        listActividades = findViewById(R.id.listActividades);
        tvActividades = findViewById(R.id.tvActividades);

        if (listActividades != null) {
            listActividades.setAdapter(taskAdapter);
        }

        // Botón Volver
        Button btnVolver = findViewById(R.id.btn_volver_m);
        if (btnVolver != null) {
            btnVolver.setOnClickListener(v -> startActivity(new Intent(this, menu_screen.class)));
        }

        // Cargar tareas
        loadTasks();
    }

    private void loadTasks() {
        taskController.getTasksForUser(new TaskController.GetTasksListener() {
            @Override
            public void onSuccess(List<Task> tasks) {
                taskList.clear();
                taskList.addAll(tasks);
                taskAdapter.updateTasks(taskList);

                // Configurar listener para cambiar estado de tareas
                taskAdapter.setOnTaskStateChangeListener((taskId, newState) -> {
                    taskController.updateTaskState(taskId, newState, 
                            new TaskController.UpdateTaskStateListener() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(task_screen.this,
                                            "Tarea marcada como completada",
                                            Toast.LENGTH_SHORT).show();
                                    // Recargar tareas para actualizar la lista
                                    loadTasks();
                                }

                                @Override
                                public void onError(String errorMessage) {
                                    Toast.makeText(task_screen.this,
                                            errorMessage,
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                });

                if (tvActividades != null) {
                    if (tasks.isEmpty()) {
                        tvActividades.setText("No hay actividades programadas");
                    } else {
                        tvActividades.setText("Actividades del día (" + tasks.size() + ")");
                    }
                }
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(task_screen.this, errorMessage, Toast.LENGTH_SHORT).show();
                if (tvActividades != null) {
                    tvActividades.setText("Error al cargar actividades");
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar tareas cuando se vuelve a la pantalla
        loadTasks();
    }
}
