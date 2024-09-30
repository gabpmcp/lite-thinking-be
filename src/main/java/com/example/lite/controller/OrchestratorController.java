package com.example.lite.controller;

import java.util.List;
import java.util.Map;

import com.example.lite.cqrs.*;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.lite.repository.EventStoreService;
import com.example.lite.util.Failure;
import com.example.lite.util.Result;
import com.example.lite.util.Success;

import static com.example.lite.cqrs.Schema.validateCommand;
import static com.example.lite.cqrs.Decision.hasPermission;
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

    @PostMapping("/orchestrate")
    public Mono<ResponseEntity<Object>> orchestrate(@RequestParam("aggregateId") String aggregateId,
                                                    @RequestBody Command command,
                                                    Authentication authentication) {
        return Mono.just(command)
                .flatMap(cmd -> hasPermission(cmd, authentication)
                        ? Mono.just(cmd)
                        : Mono.error(new RuntimeException("Access Denied")))
                .flatMap(cmd -> handleValidationResult(validateCommand(cmd), aggregateId, cmd))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    public Mono<ResponseEntity<Object>> handleValidationResult(Result<Message> result, String aggregateId, Command command) {
        return result instanceof Success<?>
                ? eventStoreService.getEventsForAggregate(aggregateId)
                .collectList()
                .flatMap(storedEvents -> {
                    List<Event> events = Decision.decide(command, Projection.project(storedEvents));
                    return eventStoreService.saveEvents(Flux.fromIterable(events), aggregateId, "")
                            .thenReturn(ResponseEntity.ok(events));
                })
                : Mono.just(ResponseEntity.badRequest().body(result));
    }
}
