package com.example.lite.util;

import java.util.Map;

public class Utils {
    // Función genérica para obtener un valor del estado con un tipo específico
    @SuppressWarnings("unchecked")
    public static <T> T getOrDefault(Map<String, Object> state, String key, T defaultValue) {
        return (T) state.getOrDefault(key, defaultValue);
    }
}
