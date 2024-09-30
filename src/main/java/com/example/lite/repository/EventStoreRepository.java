package com.example.lite.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface EventStoreRepository extends R2dbcRepository<EventEntity, String> {

    // Devuelve un Flux para representar una secuencia no bloqueante de EventEntity
    Flux<EventEntity> findByAggregateIdOrderByVersion(String aggregateId);

    // Devuelve un Flux para los eventos posteriores a una versión específica
    Flux<EventEntity> findByAggregateIdAndVersionGreaterThanOrderByVersion(String aggregateId, int version);

    // Devuelve la versión más alta de un agregado
    Mono<Integer> findMaxVersionByAggregateId(String aggregateId);
}
