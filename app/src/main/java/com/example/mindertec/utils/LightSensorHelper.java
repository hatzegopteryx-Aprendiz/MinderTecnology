package com.example.mindertec.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class LightSensorHelper implements SensorEventListener {
    private static final String TAG = "LightSensorHelper";
    private static final float LIGHT_THRESHOLD = 50.0f; // Lux - umbral al 50% de luz máxima
    
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
            // Obtener la luz máxima del sensor (típicamente 100000 lux)
            float maxLight = event.sensor.getMaximumRange();
            float lightPercentage = (lightLevel / maxLight) * 100.0f;
            
            // Si la luz es mayor al 50% → fondo blanco, sino → fondo café
            boolean shouldBeWhite = lightPercentage > LIGHT_THRESHOLD;
            
            // Solo notificar si cambió el modo
            if (shouldBeWhite != !isDarkMode) {
                isDarkMode = !shouldBeWhite; // isDarkMode = true cuando es café
                if (listener != null) {
                    listener.onLightChanged(isDarkMode);
                }
                Log.d(TAG, "Nivel de luz: " + lightLevel + " lux (" + String.format("%.1f", lightPercentage) + "%) - Fondo: " + (shouldBeWhite ? "Blanco" : "Café"));
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

