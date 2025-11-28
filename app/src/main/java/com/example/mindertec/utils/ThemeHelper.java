package com.example.mindertec.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.mindertec.R;
import com.google.android.material.card.MaterialCardView;

public class ThemeHelper {
    
    /**
     * Aplica el tema claro u oscuro a una vista raíz y sus hijos
     */
    public static void applyTheme(View rootView, boolean isDarkMode) {
        if (rootView == null) return;
        
        Context context = rootView.getContext();
        
        // Obtener colores según el modo
        int backgroundColor = ContextCompat.getColor(context, 
                isDarkMode ? R.color.background_dark : R.color.background_light);
        int cardBackgroundColor = ContextCompat.getColor(context, 
                isDarkMode ? R.color.card_background_dark : R.color.card_background_light);
        int cardSecondaryBackgroundColor = ContextCompat.getColor(context, 
                isDarkMode ? R.color.card_background_secondary_dark : R.color.card_background_secondary_light);
        int textPrimaryColor = ContextCompat.getColor(context, 
                isDarkMode ? R.color.text_primary_dark : R.color.text_primary_light);
        int textSecondaryColor = ContextCompat.getColor(context, 
                isDarkMode ? R.color.text_secondary_dark : R.color.text_secondary_light);
        int textTertiaryColor = ContextCompat.getColor(context, 
                isDarkMode ? R.color.text_tertiary_dark : R.color.text_tertiary_light);
        int buttonPrimaryColor = ContextCompat.getColor(context, 
                isDarkMode ? R.color.button_primary_dark : R.color.button_primary_light);
        int buttonSuccessColor = ContextCompat.getColor(context, 
                isDarkMode ? R.color.button_success_dark : R.color.button_success_light);
        int dividerColor = ContextCompat.getColor(context, 
                isDarkMode ? R.color.divider_dark : R.color.divider_light);
        
        // Aplicar fondo a la vista raíz
        rootView.setBackgroundColor(backgroundColor);
        
        // Aplicar colores recursivamente
        applyColorsRecursively(rootView, isDarkMode, 
                backgroundColor, cardBackgroundColor, cardSecondaryBackgroundColor,
                textPrimaryColor, textSecondaryColor, textTertiaryColor,
                buttonPrimaryColor, buttonSuccessColor, dividerColor);
    }
    
    private static void applyColorsRecursively(View view, boolean isDarkMode,
                                               int backgroundColor, int cardBackgroundColor, 
                                               int cardSecondaryBackgroundColor,
                                               int textPrimaryColor, int textSecondaryColor,
                                               int textTertiaryColor, int buttonPrimaryColor,
                                               int buttonSuccessColor, int dividerColor) {
        
        if (view == null) return;
        
        // Aplicar colores según el tipo de vista
        if (view instanceof MaterialCardView) {
            MaterialCardView card = (MaterialCardView) view;
            int currentBg = card.getCardBackgroundColor().getDefaultColor();
            // Si es blanco o beige claro, usar cardBackgroundColor, sino cardSecondaryBackgroundColor
            if (isVeryLightColor(currentBg)) {
                card.setCardBackgroundColor(cardBackgroundColor);
            } else if (isLightColor(currentBg)) {
                card.setCardBackgroundColor(cardSecondaryBackgroundColor);
            }
        } else if (view instanceof TextView) {
            TextView textView = (TextView) view;
            int currentColor = textView.getCurrentTextColor();
            
            // Cambiar colores de texto basado en el color actual
            if (matchesColor(currentColor, 0xFF5D4037) || matchesColor(currentColor, 0xFFD7CCC8)) {
                textView.setTextColor(textPrimaryColor);
            } else if (matchesColor(currentColor, 0xFF8D6E63) || matchesColor(currentColor, 0xFFBCAAA4)) {
                textView.setTextColor(textSecondaryColor);
            } else if (matchesColor(currentColor, 0xFF6D4C41) || matchesColor(currentColor, 0xFFA1887F)) {
                textView.setTextColor(textTertiaryColor);
            }
        } else if (view instanceof com.google.android.material.button.MaterialButton) {
            com.google.android.material.button.MaterialButton button = 
                    (com.google.android.material.button.MaterialButton) view;
            
            if (button.getBackgroundTintList() != null) {
                int currentTint = button.getBackgroundTintList().getDefaultColor();
                
                if (matchesColor(currentTint, 0xFF6D4C41) || matchesColor(currentTint, 0xFF8D6E63)) {
                    button.setBackgroundTint(android.content.res.ColorStateList.valueOf(buttonPrimaryColor));
                } else if (matchesColor(currentTint, 0xFF4CAF50) || matchesColor(currentTint, 0xFF66BB6A)) {
                    button.setBackgroundTint(android.content.res.ColorStateList.valueOf(buttonSuccessColor));
                }
            }
        }
        
        // Aplicar recursivamente a los hijos
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                applyColorsRecursively(group.getChildAt(i), isDarkMode,
                        backgroundColor, cardBackgroundColor, cardSecondaryBackgroundColor,
                        textPrimaryColor, textSecondaryColor, textTertiaryColor,
                        buttonPrimaryColor, buttonSuccessColor, dividerColor);
            }
        }
    }
    
    private static boolean isVeryLightColor(int color) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        // Blanco o muy claro
        return (r + g + b) > 700;
    }
    
    private static boolean isLightColor(int color) {
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        // Color claro (beige, gris claro)
        return (r + g + b) > 400;
    }
    
    private static boolean matchesColor(int color1, int color2) {
        // Comparar colores con un margen de tolerancia
        return Math.abs(color1 - color2) < 0x1000000;
    }
}

