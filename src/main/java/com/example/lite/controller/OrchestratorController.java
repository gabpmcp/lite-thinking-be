package com.example.lite.controller;

import java.util.List;

import com.example.lite.cqrs.*;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.lite.repository.EventStoreService;
import com.example.lite.util.Result;
import com.example.lite.util.Success;

import static com.example.lite.cqrs.Schema.validateCommand;
import com.example.lite.util.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/orchestrator")
public class OrchestratorController {

    private final EventStoreService eventStoreService;

    public OrchestratorController(EventStoreService eventStoreService) {
        this.eventStoreService = eventStoreService;
    }

    @PostMapping("/commands")
    public Mono<ResponseEntity<Object>> orchestrate(@RequestParam("aggregateId") String aggregateId,
                                                    @RequestBody Command command,
                                                    Authentication authentication) {
        System.out.println(aggregateId + " " + command.toString());
        return Mono.just(command)
                .flatMap(cmd -> handleValidationResult(validateCommand(cmd), aggregateId, cmd))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    public Mono<ResponseEntity<Object>> handleValidationResult(Result<Message> result, String aggregateId, Command command) {
        if (result instanceof Success<?>) {
            return eventStoreService.findByAggregateIdOrderByVersion(aggregateId)
                    .collectList()
                    .flatMap(storedEvents -> {
                        List<Event> decisionEvents = Decision.decide(command, Projection.project(storedEvents));

                        // Filtrar todos los ErrorEvent
                        List<ErrorEvent> errorEvents = decisionEvents.stream()
                                .filter(event -> event instanceof ErrorEvent)
                                .map(event -> (ErrorEvent) event)
                                .toList();

                        // Si hay errores, devolver la lista de errores
                        if (!errorEvents.isEmpty()) {
                            List<String> errorMessages = errorEvents.stream()
                                    .map(ErrorEvent::error)
                                    .toList();
                            return Mono.just(ResponseEntity.status(409).body(errorMessages));
                        }

                        // Si no hay errores, proceder con la lógica normal
                        return eventStoreService.saveEvents(Flux.fromIterable(decisionEvents), aggregateId, "")
                                .thenReturn(ResponseEntity.ok(storedEvents));
                    });
        } else {
            return Mono.just(ResponseEntity.badRequest().body(result));
        }
    }
}
