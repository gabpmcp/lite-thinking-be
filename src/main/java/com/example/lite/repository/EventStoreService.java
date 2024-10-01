package com.example.lite.repository;

import com.example.lite.cqrs.Event;
import com.example.lite.util.Utils;

import io.vavr.control.Option;
import io.vavr.control.Try;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.lite.util.Utils.serialize;

@Service
public class EventStoreService {

    private final EventStoreRepository eventStoreRepository;

    public EventStoreService(EventStoreRepository eventStoreRepository) {
        this.eventStoreRepository = eventStoreRepository;
    }

    public Flux<Event> findByAggregateIdOrderByVersion(String aggregateId) {
        return eventStoreRepository.findByAggregateIdOrderByVersion(aggregateId)
                .flatMap(entity -> Flux.fromStream(convertEventEntityToEvent(entity).toJavaStream())); // Convertir Option<Event> en Flux
    }

    // Función pura que envuelve la deserialización y gestiona errores con Vavr
    public Option<Event> convertEventEntityToEvent(EventEntity entity) {
        return Try.of(() -> Utils.getObjectMapper().readValue(entity.eventData(), Event.class))
                .toOption(); // Convertir Try a Option: Some si es exitoso, None si hay error
    }

    //Serializa y guarda una lista de eventos polimórficos, con versión autoincremental y metadata opcional
    public Mono<List<Event>> saveEvents(Flux<Event> events, String aggregateId, String metadata) {
        return eventStoreRepository.findMaxVersionByAggregateId(aggregateId)
                .defaultIfEmpty(0)
                .flatMapMany(currentVersion -> {
                    AtomicInteger versionCounter = new AtomicInteger(currentVersion);

                    // Mapear cada evento a un EventEntity y guardarlo
                    return events.flatMap(event -> {
                        EventEntity eventEntity = new EventEntity(
                                aggregateId,
                                event.getClass().getSimpleName(),
                                serialize(event), // Serializar el evento en JSON
                                versionCounter.incrementAndGet(), // Autoincremento de la versión
                                metadata
                        );
                        // Guardar el EventEntity y devolver el evento original para seguir la composición
                        return eventStoreRepository.save(eventEntity).thenReturn(event);
                    });
                })
                .collectList(); // Convertir el resultado final en una lista de eventos
    }
}
