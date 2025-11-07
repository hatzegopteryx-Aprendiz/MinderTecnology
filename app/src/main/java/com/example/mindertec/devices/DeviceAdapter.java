package com.example.mindertec.devices;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mindertec.R;
import com.example.mindertec.models.Device;

import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private List<Device> deviceList;

    public DeviceAdapter(List<Device> deviceList) {
        this.deviceList = deviceList;
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_equipo, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        Device device = deviceList.get(position);
        
        // Manejar valores null para evitar crashes
        String nombre = device.getNombre() != null ? device.getNombre() : "Sin nombre";
        String marca = device.getMarca() != null ? device.getMarca() : "Sin marca";
        String descripcion = device.getDescripcion() != null ? device.getDescripcion() : "Sin descripción";
        String ultimaRevision = device.getUltimaRevision() != null ? device.getUltimaRevision() : "Sin fecha";
        String deviceId = device.getDeviceId() != null ? device.getDeviceId() : "N/A";
        
        // Actualizar los TextViews con los datos del dispositivo
        if (holder.tvIdEquipo != null) {
            holder.tvIdEquipo.setText("Equipo: " + nombre + " | Marca: " + marca + " | ID: " + deviceId);
        }
        if (holder.tvDescripcion != null) {
            holder.tvDescripcion.setText("Descripción: " + descripcion);
        }
        if (holder.tvUltimaRevision != null) {
            holder.tvUltimaRevision.setText("Última revisión: " + ultimaRevision);
        }
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder {
        TextView tvIdEquipo, tvDescripcion, tvUltimaRevision;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIdEquipo = itemView.findViewById(R.id.tvIdEquipo);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvUltimaRevision = itemView.findViewById(R.id.tvUltimaRevision);
        }
    }
}
