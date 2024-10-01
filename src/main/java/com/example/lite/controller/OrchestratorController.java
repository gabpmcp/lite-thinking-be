package com.example.lite.controller;

import java.util.List;

import com.example.lite.cqrs.*;
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
        return result instanceof Success<?>
                ? eventStoreService.findByAggregateIdOrderByVersion(aggregateId)
                .collectList()
                .flatMap(storedEvents -> eventStoreService.saveEvents(
                                Flux.fromIterable(Decision.decide(command, Projection.project(storedEvents))), aggregateId, "")
                        .thenReturn(ResponseEntity.ok(storedEvents))
                )
                : Mono.just(ResponseEntity.badRequest().body(result));
    }
}
