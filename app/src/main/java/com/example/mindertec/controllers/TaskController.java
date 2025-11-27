package com.example.mindertec.controllers;

import android.content.Context;

import com.example.mindertec.controllers.DeviceController;
import com.example.mindertec.models.Task;
import com.example.mindertec.repositories.TaskRepository;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TaskController {
    private TaskRepository taskRepository;

    public TaskController(Context context) {
        this.taskRepository = new TaskRepository(context);
    }

    public interface SaveTaskListener {
        void onSaveSuccess(String taskId);
        void onSaveError(String errorMessage);
        void onValidationError(String field, String message);
    }

    public interface GetTasksListener {
        void onSuccess(List<Task> tasks);
        void onError(String errorMessage);
    }

    public interface UpdateTaskStateListener {
        void onSuccess();
        void onError(String errorMessage);
    }

    private DeviceController deviceController;

    public void setDeviceController(DeviceController deviceController) {
        this.deviceController = deviceController;
    }

    public void saveTask(String deviceId, String deviceName, String descripcion,
                        String fechaInicio, String fechaInicioIso,
                        String fechaTerminoEstimada, String fechaTerminoEstimadaIso,
                        String tecnicoId, String tecnicoEmail, String tecnicoNombre,
                        SaveTaskListener listener) {
        
        // Validaciones
        if (descripcion == null || descripcion.trim().isEmpty()) {
            listener.onValidationError("descripcion", "La descripción es requerida");
            return;
        }

        if (fechaTerminoEstimada == null || fechaTerminoEstimada.trim().isEmpty()) {
            listener.onValidationError("fechaTermino", "La fecha estimada de término es requerida");
            return;
        }

        if (deviceId == null || deviceId.trim().isEmpty()) {
            listener.onSaveError("ID de dispositivo no válido");
            return;
        }

        // Crear objeto Task
        Task task = new Task(deviceId, deviceName, descripcion,
                fechaInicio, fechaInicioIso,
                fechaTerminoEstimada, fechaTerminoEstimadaIso,
                tecnicoId, tecnicoEmail, tecnicoNombre);

        // Guardar tarea
        taskRepository.saveTask(task, new TaskRepository.SaveTaskCallback() {
            @Override
            public void onSuccess(String taskId) {
                listener.onSaveSuccess(taskId);
            }

            @Override
            public void onError(String errorMessage) {
                listener.onSaveError(errorMessage);
            }
        });
    }

    public void getTasksForUser(GetTasksListener listener) {
        taskRepository.getTasksForUser(new TaskRepository.GetTasksCallback() {
            @Override
            public void onSuccess(List<Task> tasks) {
                listener.onSuccess(tasks);
            }

            @Override
            public void onError(String errorMessage) {
                listener.onError(errorMessage);
            }
        });
    }

    public void updateTaskState(String taskId, String newState, UpdateTaskStateListener listener) {
        if (taskId == null || taskId.trim().isEmpty()) {
            listener.onError("ID de tarea no válido");
            return;
        }

        if (newState == null || newState.trim().isEmpty()) {
            listener.onError("Estado no válido");
            return;
        }

        // Validar que el estado sea uno de los permitidos
        if (!"pendiente".equals(newState) && !"en_proceso".equals(newState) && !"completada".equals(newState)) {
            listener.onError("Estado no válido. Debe ser: pendiente, en_proceso o completada");
            return;
        }

        taskRepository.updateTaskState(taskId, newState, new TaskRepository.UpdateTaskStateCallback() {
            @Override
            public void onSuccess(Task task) {
                // Si la tarea se completó, guardarla en el historial del dispositivo
                if ("completada".equals(newState) && deviceController != null && task != null) {
                    String deviceId = task.getDeviceId();
                    String descripcion = task.getDescripcion();
                    String tecnicoNombre = task.getTecnicoNombre();
                    
                    if (deviceId != null && descripcion != null && tecnicoNombre != null) {
                        // Obtener fecha actual
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        String fechaCompletada = dateFormat.format(Calendar.getInstance().getTime());
                        
                        // Guardar en el historial del dispositivo
                        deviceController.addCompletedTask(deviceId, descripcion, fechaCompletada, tecnicoNombre,
                                new DeviceController.AddCompletedTaskListener() {
                                    @Override
                                    public void onSuccess() {
                                        listener.onSuccess();
                                    }

                                    @Override
                                    public void onError(String errorMessage) {
                                        // Aunque falle guardar en el historial, la tarea ya se completó
                                        // Por lo tanto, consideramos éxito
                                        listener.onSuccess();
                                    }
                                });
                    } else {
                        listener.onSuccess();
                    }
                } else {
                    listener.onSuccess();
                }
            }

            @Override
            public void onError(String errorMessage) {
                listener.onError(errorMessage);
            }
        });
    }
}

