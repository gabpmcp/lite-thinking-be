package com.example.lite.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

public interface EventStoreRepository extends ReactiveCrudRepository<EventEntity, String> {

    // Devuelve un Flux para representar una secuencia no bloqueante de EventEntity
    @Query("SELECT id, aggregate_id, event_type, event_data, version, created_at, metadata FROM events WHERE aggregate_id = :aggregateId ORDER BY version ASC")
    Flux<EventEntity> findByAggregateIdOrderByVersion(@Param("aggregateId") String aggregateId);

    // Devuelve un Flux para los eventos posteriores a una versión específica
    Flux<EventEntity> findByAggregateIdAndVersionGreaterThanOrderByVersion(String aggregateId, int version);

    // Devuelve la versión más alta de un agregado
    @Query("SELECT COALESCE(MAX(version), 0) FROM events WHERE aggregate_id = :aggregateId")
    Mono<Integer> findMaxVersionByAggregateId(String aggregateId);

    @Query("INSERT INTO events (id, aggregate_id, event_type, event_data, version, created_at, metadata) " +
            "VALUES (:id, :aggregateId, :eventType, CAST(:eventData AS jsonb), :version, :createdAt, CAST(:metadata AS jsonb))")
    Mono<Void> insertEvent(@Param("id") UUID id,
                           @Param("aggregateId") String aggregateId,
                           @Param("eventType") String eventType,
                           @Param("eventData") String eventData,  // Debe ser un JSON String válido
                           @Param("version") int version,
                           @Param("createdAt") Instant createdAt,
                           @Param("metadata") String metadata);  // Debe ser un JSON String válido
}
