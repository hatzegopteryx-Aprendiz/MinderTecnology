package com.example.mindertec.devices;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mindertec.R;
import com.example.mindertec.models.CompletedTask;

import java.util.List;

public class CompletedTaskAdapter extends BaseAdapter {
    private List<CompletedTask> completedTaskList;

    public CompletedTaskAdapter(List<CompletedTask> completedTaskList) {
        this.completedTaskList = completedTaskList;
    }

    @Override
    public int getCount() {
        return completedTaskList != null ? completedTaskList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return completedTaskList != null ? completedTaskList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_completed_task, parent, false);
        }

        CompletedTask completedTask = completedTaskList.get(position);

        TextView tvTarea = convertView.findViewById(R.id.tvTarea);
        TextView tvFecha = convertView.findViewById(R.id.tvFecha);
        TextView tvPersonal = convertView.findViewById(R.id.tvPersonal);

        if (tvTarea != null) {
            tvTarea.setText(completedTask.getTarea() != null ? completedTask.getTarea() : "Sin descripción");
        }

        if (tvFecha != null) {
            String fecha = completedTask.getFecha() != null ? completedTask.getFecha() : "Sin fecha";
            tvFecha.setText("Fecha: " + fecha);
        }

        if (tvPersonal != null) {
            String personal = completedTask.getPersonal() != null ? completedTask.getPersonal() : "Sin información";
            tvPersonal.setText("Personal: " + personal);
        }

        return convertView;
    }

    public void updateTasks(List<CompletedTask> newTaskList) {
        this.completedTaskList = newTaskList;
        notifyDataSetChanged();
    }
}

