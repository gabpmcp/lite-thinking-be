package com.example.lite.repository;

import java.util.ArrayList;
import java.util.List;

import com.example.lite.cqrs.Event;

public class EventStore {
    final static List<Event> events = new ArrayList<>();

    // Simulación de almacenamiento persistente
    public static List<Event> getStoredEvents() {
        // Simula la obtención de eventos almacenados de un repositorio o base de datos
        return events;
    }

    public static List<Event> saveEvents(List<Event> events) {
        // Simula la obtención de eventos almacenados de un repositorio o base de datos
        return events;
    }
}
