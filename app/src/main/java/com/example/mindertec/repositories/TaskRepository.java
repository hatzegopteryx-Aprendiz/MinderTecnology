package com.example.mindertec.repositories;

import android.content.Context;

import com.example.mindertec.models.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TaskRepository {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    public TaskRepository(Context context) {
        this.mAuth = FirebaseAuth.getInstance();
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public interface SaveTaskCallback {
        void onSuccess(String taskId);
        void onError(String errorMessage);
    }

    public interface GetTasksCallback {
        void onSuccess(java.util.List<Task> tasks);
        void onError(String errorMessage);
    }

    public interface UpdateTaskStateCallback {
        void onSuccess(Task task);
        void onError(String errorMessage);
    }

    public interface GetTaskCallback {
        void onSuccess(Task task);
        void onError(String errorMessage);
    }

    public void saveTask(Task task, SaveTaskCallback callback) {
        if (mAuth.getCurrentUser() == null) {
            callback.onError("Error: Usuario no autenticado. Por favor, inicia sesión");
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        String taskId = mDatabase.child("Tareas").child(userId).push().getKey();
        
        if (taskId == null) {
            callback.onError("Error al generar ID de la tarea");
            return;
        }

        task.setTaskId(taskId);

        // Guardar en Firebase Database
        mDatabase.child("Tareas").child(userId).child(taskId)
                .setValue(task)
                .addOnCompleteListener(taskResult -> {
                    if (taskResult.isSuccessful()) {
                        callback.onSuccess(taskId);
                    } else {
                        String errorMessage = translateFirebaseError(taskResult.getException());
                        callback.onError("Error al guardar tarea: " + errorMessage);
                    }
                });
    }

    public void getTasksForUser(GetTasksCallback callback) {
        if (mAuth.getCurrentUser() == null) {
            callback.onError("Error: Usuario no autenticado");
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference tasksRef = mDatabase.child("Tareas").child(userId);

        tasksRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {
                java.util.List<Task> tasks = new java.util.ArrayList<>();
                
                if (snapshot.exists() && snapshot.hasChildren()) {
                    for (com.google.firebase.database.DataSnapshot taskSnap : snapshot.getChildren()) {
                        Task task = taskSnap.getValue(Task.class);
                        if (task != null) {
                            if (task.getTaskId() == null || task.getTaskId().isEmpty()) {
                                task.setTaskId(taskSnap.getKey());
                            }
                            tasks.add(task);
                        }
                    }
                }
                
                callback.onSuccess(tasks);
            }

            @Override
            public void onCancelled(com.google.firebase.database.DatabaseError error) {
                String errorMessage = translateFirebaseError(error.toException());
                callback.onError("Error al leer tareas: " + errorMessage);
            }
        });
    }

    public void getTask(String taskId, GetTaskCallback callback) {
        if (mAuth.getCurrentUser() == null) {
            callback.onError("Error: Usuario no autenticado");
            return;
        }

        if (taskId == null || taskId.isEmpty()) {
            callback.onError("Error: ID de tarea no válido");
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference taskRef = mDatabase.child("Tareas").child(userId).child(taskId);

        taskRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Task task = snapshot.getValue(Task.class);
                    if (task != null) {
                        if (task.getTaskId() == null || task.getTaskId().isEmpty()) {
                            task.setTaskId(taskId);
                        }
                        callback.onSuccess(task);
                    } else {
                        callback.onError("Error al cargar datos de la tarea");
                    }
                } else {
                    callback.onError("Tarea no encontrada");
                }
            }

            @Override
            public void onCancelled(com.google.firebase.database.DatabaseError error) {
                String errorMessage = translateFirebaseError(error.toException());
                callback.onError("Error al leer tarea: " + errorMessage);
            }
        });
    }

    public void updateTaskState(String taskId, String newState, UpdateTaskStateCallback callback) {
        if (mAuth.getCurrentUser() == null) {
            callback.onError("Error: Usuario no autenticado. Por favor, inicia sesión");
            return;
        }

        if (taskId == null || taskId.isEmpty()) {
            callback.onError("Error: ID de tarea no válido");
            return;
        }

        if (newState == null || newState.trim().isEmpty()) {
            callback.onError("Error: Estado no válido");
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference taskRef = mDatabase.child("Tareas").child(userId).child(taskId);

        // Primero obtener la tarea para devolverla actualizada
        getTask(taskId, new GetTaskCallback() {
            @Override
            public void onSuccess(Task task) {
                // Actualizar solo el campo estado
                java.util.HashMap<String, Object> updates = new java.util.HashMap<>();
                updates.put("estado", newState);

                taskRef.updateChildren(updates)
                        .addOnCompleteListener(updateTask -> {
                            if (updateTask.isSuccessful()) {
                                // Actualizar el estado en el objeto task
                                task.setEstado(newState);
                                callback.onSuccess(task);
                            } else {
                                String errorMessage = translateFirebaseError(updateTask.getException());
                                callback.onError("Error al actualizar estado: " + errorMessage);
                            }
                        });
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    private String translateFirebaseError(Exception exception) {
        if (exception == null) {
            return "Error desconocido";
        }
        
        String exceptionMessage = exception.getMessage();
        if (exceptionMessage == null) {
            return "Error desconocido";
        }
        
        if (exceptionMessage.contains("network") || exceptionMessage.contains("Network")) {
            return "Error de conexión. Verifica tu internet";
        } else if (exceptionMessage.contains("permission") || exceptionMessage.contains("Permission")) {
            return "No tienes permiso para realizar esta acción";
        } else if (exceptionMessage.contains("timeout") || exceptionMessage.contains("Timeout")) {
            return "Tiempo de espera agotado. Intenta nuevamente";
        } else {
            return exceptionMessage;
        }
    }
}

