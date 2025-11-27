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

    public interface OnTaskStateChangeListener {
        void onTaskStateChanged(String taskId, String newState);
    }

    public TaskAdapter(List<Task> taskList) {
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
                btnTerminar.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onTaskStateChanged(taskId, "completada");
                    }
                });
            } else {
                btnTerminar.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    public void updateTasks(List<Task> newTaskList) {
        this.taskList = newTaskList;
        notifyDataSetChanged();
    }
}

