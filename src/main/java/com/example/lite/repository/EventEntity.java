package com.example.lite.repository;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("events") // Mapea el record a la tabla 'events'
public record EventEntity(
        @Id UUID id,
        String aggregateId,
        String eventType,
        String eventData,
        int version,
        Instant timestamp,
        String metadata) {

    public EventEntity(String aggregateId, String eventType, String eventData, int version, String metadata) {
        this(UUID.randomUUID(), aggregateId, eventType, eventData, version, Instant.now(), metadata);
    }
}
