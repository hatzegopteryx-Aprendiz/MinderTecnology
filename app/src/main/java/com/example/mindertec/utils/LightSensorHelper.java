package com.example.mindertec.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class LightSensorHelper implements SensorEventListener {
    private static final String TAG = "LightSensorHelper";
    private static final float LIGHT_THRESHOLD = 50.0f; // Lux - umbral para cambiar entre claro y oscuro
    
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private LightChangeListener listener;
    private boolean isDarkMode = false;
    
    public interface LightChangeListener {
        void onLightChanged(boolean isDarkMode);
    }
    
    public LightSensorHelper(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }
    }
    
    public boolean isSensorAvailable() {
        return lightSensor != null;
    }
    
    public void setLightChangeListener(LightChangeListener listener) {
        this.listener = listener;
    }
    
    public void startListening() {
        if (lightSensor != null && sensorManager != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
            Log.d(TAG, "Sensor de luz iniciado");
        } else {
            Log.w(TAG, "Sensor de luz no disponible en este dispositivo");
        }
    }
    
    public void stopListening() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
            Log.d(TAG, "Sensor de luz detenido");
        }
    }
    
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float lightLevel = event.values[0];
            boolean shouldBeDark = lightLevel < LIGHT_THRESHOLD;
            
            // Solo notificar si cambió el modo
            if (shouldBeDark != isDarkMode) {
                isDarkMode = shouldBeDark;
                if (listener != null) {
                    listener.onLightChanged(isDarkMode);
                }
                Log.d(TAG, "Nivel de luz: " + lightLevel + " lux - Modo: " + (isDarkMode ? "Oscuro" : "Claro"));
            }
        }
    }
    
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No necesitamos hacer nada aquí
    }
    
    public boolean isDarkMode() {
        return isDarkMode;
    }
}

