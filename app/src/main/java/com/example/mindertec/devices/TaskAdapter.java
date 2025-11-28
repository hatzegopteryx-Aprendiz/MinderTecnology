package com.example.mindertec.devices;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mindertec.R;
import com.example.mindertec.models.Task;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class TaskAdapter extends BaseAdapter {
    private List<Task> taskList;
    private OnTaskStateChangeListener listener;
    private android.content.Context context;
    private java.util.Set<String> processingTasks = new java.util.HashSet<>(); // Para evitar duplicados

    public interface OnTaskStateChangeListener {
        void onTaskStateChanged(String taskId, String newState, String descripcionRealizada);
    }

    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    public TaskAdapter(android.content.Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    public void setOnTaskStateChangeListener(OnTaskStateChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return taskList != null ? taskList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return taskList != null ? taskList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_task, parent, false);
        }
        
        // Guardar contexto si no está establecido
        if (context == null) {
            context = parent.getContext();
        }

        Task task = taskList.get(position);

        TextView tvDeviceName = convertView.findViewById(R.id.tvDeviceName);
        TextView tvDescripcion = convertView.findViewById(R.id.tvDescripcion);
        TextView tvFechaInicio = convertView.findViewById(R.id.tvFechaInicio);
        TextView tvFechaTermino = convertView.findViewById(R.id.tvFechaTermino);
        TextView tvEstado = convertView.findViewById(R.id.tvEstado);
        MaterialButton btnTerminar = convertView.findViewById(R.id.btnTerminar);

        if (tvDeviceName != null) {
            tvDeviceName.setText(task.getDeviceName() != null ? task.getDeviceName() : "Sin nombre");
        }

        if (tvDescripcion != null) {
            tvDescripcion.setText(task.getDescripcion() != null ? task.getDescripcion() : "Sin descripción");
        }

        if (tvFechaInicio != null) {
            String fechaInicio = task.getFechaInicio() != null ? task.getFechaInicio() : "Sin fecha";
            tvFechaInicio.setText("Inicio: " + fechaInicio);
        }

        if (tvFechaTermino != null) {
            String fechaTermino = task.getFechaTerminoEstimada() != null ? task.getFechaTerminoEstimada() : "Sin fecha";
            tvFechaTermino.setText("Término: " + fechaTermino);
        }

        if (tvEstado != null) {
            String estado = task.getEstado() != null ? task.getEstado() : "pendiente";
            String estadoTexto = "";
            int colorEstado = 0xFF000000;

            switch (estado) {
                case "pendiente":
                    estadoTexto = "Pendiente";
                    colorEstado = 0xFFFF9800; // Naranja
                    break;
                case "en_proceso":
                    estadoTexto = "En Proceso";
                    colorEstado = 0xFF2196F3; // Azul
                    break;
                case "completada":
                    estadoTexto = "Completada";
                    colorEstado = 0xFF4CAF50; // Verde
                    break;
                default:
                    estadoTexto = "Pendiente";
                    colorEstado = 0xFFFF9800;
            }

            tvEstado.setText(estadoTexto);
            tvEstado.setTextColor(colorEstado);
        }

        // Configurar botón Terminar
        if (btnTerminar != null) {
            String estado = task.getEstado() != null ? task.getEstado() : "pendiente";
            String taskId = task.getTaskId();
            
            // Mostrar botón solo si la tarea está pendiente o en proceso
            if (("pendiente".equals(estado) || "en_proceso".equals(estado)) && taskId != null) {
                btnTerminar.setVisibility(View.VISIBLE);
                
                // Deshabilitar si ya está siendo procesada
                boolean isProcessing = processingTasks.contains(taskId);
                btnTerminar.setEnabled(!isProcessing);
                if (isProcessing) {
                    btnTerminar.setText("Procesando...");
                } else {
                    btnTerminar.setText("Terminar");
                }
                
                btnTerminar.setOnClickListener(v -> {
                    // Prevenir múltiples clics
                    if (processingTasks.contains(taskId)) {
                        return; // Ya está siendo procesada
                    }
                    
                    // Marcar como procesando inmediatamente
                    processingTasks.add(taskId);
                    btnTerminar.setEnabled(false);
                    btnTerminar.setText("Procesando...");
                    
                    // Mostrar diálogo para ingresar descripción
                    showCompleteTaskDialog(parent.getContext(), taskId, task, btnTerminar);
                });
            } else {
                btnTerminar.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    public void updateTasks(List<Task> newTaskList) {
        this.taskList = newTaskList;
        // Limpiar tareas completadas del set de procesamiento
        processingTasks.clear();
        notifyDataSetChanged();
    }

    private void showCompleteTaskDialog(android.content.Context context, String taskId, Task task, MaterialButton btnTerminar) {
        android.app.Dialog dialog = new android.app.Dialog(context);
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_completar_tarea);
        dialog.setCancelable(true);

        // Configurar ventana del diálogo
        android.view.Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
            window.setGravity(android.view.Gravity.CENTER);
        }

        com.google.android.material.textfield.TextInputEditText etDescripcion = dialog.findViewById(R.id.etDescripcionRealizada);
        com.google.android.material.button.MaterialButton btnCancelar = dialog.findViewById(R.id.btnCancelar);
        com.google.android.material.button.MaterialButton btnConfirmar = dialog.findViewById(R.id.btnConfirmar);

        if (btnCancelar != null) {
            btnCancelar.setOnClickListener(v -> {
                // Restaurar botón si se cancela
                processingTasks.remove(taskId);
                dialog.dismiss();
                notifyDataSetChanged(); // Refrescar para restaurar el botón
            });
        }

        if (btnConfirmar != null) {
            btnConfirmar.setOnClickListener(v -> {
                String descripcionRealizada = etDescripcion != null ? etDescripcion.getText().toString().trim() : "";
                
                if (descripcionRealizada.isEmpty()) {
                    android.widget.Toast.makeText(context, "Por favor, ingrese una descripción de la tarea realizada", 
                            android.widget.Toast.LENGTH_SHORT).show();
                    if (etDescripcion != null) {
                        etDescripcion.requestFocus();
                    }
                    return;
                }

                // Cerrar diálogo
                dialog.dismiss();

                // Llamar al listener con la descripción
                if (listener != null) {
                    listener.onTaskStateChanged(taskId, "completada", descripcionRealizada);
                }
            });
        }

        dialog.show();
    }
}

