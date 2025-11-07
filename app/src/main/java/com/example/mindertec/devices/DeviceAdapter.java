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
        holder.tvNombreEquipo.setText("Equipo: " + device.getNombre());
        holder.tvMarca.setText("Marca: " + device.getMarca());
        holder.tvDescripcion.setText("Descripción: " + device.getDescripcion());
        holder.tvUltimaRevision.setText("Última revisión: " + device.getUltimaRevision());
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreEquipo, tvMarca, tvDescripcion, tvUltimaRevision;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreEquipo = itemView.findViewById(R.id.tvNombreEquipo);
            tvMarca = itemView.findViewById(R.id.tvMarca);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionEquipo);
            tvUltimaRevision = itemView.findViewById(R.id.tvUltimaRevisionEquipo);
        }
    }
}
