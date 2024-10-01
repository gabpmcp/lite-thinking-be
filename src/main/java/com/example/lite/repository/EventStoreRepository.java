package com.example.lite.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventStoreRepository extends ReactiveCrudRepository<EventEntity, String> {

    // Devuelve un Flux para representar una secuencia no bloqueante de EventEntity
    @Query("SELECT id, aggregate_id, event_type, event_data, version, created_at, metadata FROM events WHERE aggregate_id = :aggregateId ORDER BY version ASC")
    Flux<EventEntity> findByAggregateIdOrderByVersion(@Param("aggregateId") String aggregateId);

    // Devuelve un Flux para los eventos posteriores a una versión específica
    Flux<EventEntity> findByAggregateIdAndVersionGreaterThanOrderByVersion(String aggregateId, int version);

    // Devuelve la versión más alta de un agregado
    Mono<Integer> findMaxVersionByAggregateId(String aggregateId);
}
