package com.example.mindertec.utils;

import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ThemeHelper {
    
    // Colores de fondo
    private static final int COLOR_BACKGROUND_WHITE = 0xFFFFFFFF; // Blanco
    private static final int COLOR_BACKGROUND_BROWN = 0xFF6D4C41; // Café
    
    // Colores para tarjetas de tareas
    private static final int COLOR_TASK_CARD_WHITE = 0xFFFFFFFF; // Blanco
    private static final int COLOR_TASK_CARD_BROWN = 0xFF6D4C41; // Café
    private static final int COLOR_TASK_TEXT_WHITE = 0xFFFFFFFF; // Blanco
    private static final int COLOR_TASK_TEXT_BROWN = 0xFF6D4C41; // Café
    private static final int COLOR_TASK_TEXT_BROWN_DARK = 0xFF5D4037; // Café oscuro
    private static final int COLOR_TASK_TEXT_BROWN_MEDIUM = 0xFF8D6E63; // Café medio
    
    /**
     * Cambia solo el fondo de la vista raíz según la luz
     * @param rootView Vista raíz a la que cambiar el fondo
     * @param isDarkMode true = fondo café, false = fondo blanco
     */
    public static void changeBackgroundOnly(View rootView, boolean isDarkMode) {
        if (rootView == null) return;
        
        // Si isDarkMode es true → fondo café, si es false → fondo blanco
        int backgroundColor = isDarkMode ? COLOR_BACKGROUND_BROWN : COLOR_BACKGROUND_WHITE;
        rootView.setBackgroundColor(backgroundColor);
    }
    
    /**
     * Aplica tema a una tarjeta de tarea según la luz
     * @param taskCardView Vista de la tarjeta de tarea (LinearLayout dentro del CardView)
     * @param isDarkMode true = poca luz (tarjeta café con letras blancas), false = mucha luz (tarjeta blanca con letras café)
     */
    public static void applyTaskCardTheme(View taskCardView, boolean isDarkMode) {
        if (taskCardView == null) return;
        
        // Cambiar fondo de la tarjeta
        int cardBackgroundColor = isDarkMode ? COLOR_TASK_CARD_BROWN : COLOR_TASK_CARD_WHITE;
        taskCardView.setBackgroundColor(cardBackgroundColor);
        
        // Cambiar colores de los textos
        if (taskCardView instanceof LinearLayout) {
            LinearLayout layout = (LinearLayout) taskCardView;
            for (int i = 0; i < layout.getChildCount(); i++) {
                View child = layout.getChildAt(i);
                applyTextColorsRecursive(child, isDarkMode);
            }
        }
    }
    
    private static void applyTextColorsRecursive(View view, boolean isDarkMode) {
        if (view == null) return;
        
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            int currentColor = textView.getCurrentTextColor();
            
            // Si es un TextView, cambiar el color según el modo
            // Mantener colores especiales (verde para fecha término, naranja/azul para estado)
            if (!isSpecialColor(currentColor)) {
                // Cambiar a blanco si es modo oscuro, o café si es modo claro
                int newColor = isDarkMode ? COLOR_TASK_TEXT_WHITE : getBrownTextColor(currentColor);
                textView.setTextColor(newColor);
            }
        } else if (view instanceof LinearLayout) {
            LinearLayout layout = (LinearLayout) view;
            for (int i = 0; i < layout.getChildCount(); i++) {
                applyTextColorsRecursive(layout.getChildAt(i), isDarkMode);
            }
        }
    }
    
    private static boolean isSpecialColor(int color) {
        // Colores que no deben cambiar: verde (#4CAF50), naranja (#FF9800), azul (#2196F3)
        return color == 0xFF4CAF50 || color == 0xFFFF9800 || color == 0xFF2196F3;
    }
    
    private static int getBrownTextColor(int originalColor) {
        // Mapear colores café originales
        if (originalColor == 0xFF5D4037 || originalColor == 0xFFFFFFFF) {
            return COLOR_TASK_TEXT_BROWN_DARK; // #5D4037
        } else if (originalColor == 0xFF8D6E63) {
            return COLOR_TASK_TEXT_BROWN_MEDIUM; // #8D6E63
        } else if (originalColor == 0xFF6D4C41) {
            return COLOR_TASK_TEXT_BROWN; // #6D4C41
        }
        return COLOR_TASK_TEXT_BROWN_DARK; // Por defecto
    }
}
