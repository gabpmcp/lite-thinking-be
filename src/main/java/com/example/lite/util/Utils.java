package com.example.lite.util;

import io.vavr.collection.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;

public final class Utils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // Memoized ObjectMapper instance
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    // Función genérica para obtener un valor del estado con un tipo específico
    @SuppressWarnings("unchecked")
    public static <T> T getOrDefault(Map<String, Object> state, String key, T defaultValue) {
        return (T) state.getOrDefault(key, defaultValue);
    }

    // Serialización genérica para cualquier tipo T, con un resultado en forma de Map
    public static <T> String serialize(T object) {
        return Try.of(() -> OBJECT_MAPPER.writeValueAsString(object))
                .fold(error -> "", json -> json);
    }
}
