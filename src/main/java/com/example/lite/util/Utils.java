package com.example.lite.util;

import io.vavr.collection.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Option;
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

    // Función de alto orden que carga una variable de entorno y pasa el resultado a otra función
    public static <T> T withEnv(String envVar, String defaultValue, Function<String, T> function) {
        String envValue = Option.of(System.getenv(envVar)).getOrElse(defaultValue);
        return function.apply(envValue);
    }
}
