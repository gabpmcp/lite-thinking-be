package com.example.lite.controller;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.lite.cqrs.Command;
import com.example.lite.cqrs.Decision;
import com.example.lite.cqrs.ErrorEvent;
import com.example.lite.cqrs.Event;
import com.example.lite.cqrs.Projection;
import com.example.lite.util.Failure;
import com.example.lite.util.Result;
import com.example.lite.util.Success;

import static com.example.lite.cqrs.Schema.validateCommand;
import static com.example.lite.cqrs.Decision.hasPermission;
import static com.example.lite.repository.EventStore.getStoredEvents;
import static com.example.lite.repository.EventStore.saveEvents;
import com.example.lite.util.Message;

@RestController
@RequestMapping("/orchestrator")
public class OrchestratorController {

    @PostMapping
    public ResponseEntity<Object> orchestrate(@RequestBody Command command, Authentication authentication) {
        try {
            // Verificar permisos según el tipo de comando y roles del usuario
            if (!hasPermission(command, authentication)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Failure<>(new ErrorEvent("Access Denied")));
            }

            // Validar comando y obtener resultado
            Result<Message> result = validateCommand(command);

            // Verificar si la validación fue exitosa o fallida
            if (result instanceof Success<?> success && success.value() instanceof Command) {
                // Obtener eventos almacenados antes de la primera proyección
                List<Event> storedEvents = getStoredEvents();

                // Decidir el evento basado en el comando y el estado actual
                List<Event> events = Decision.decide(command, Projection.project(storedEvents));

                // Persistir los eventos en el repositorio o base de datos
                saveEvents(events);

                // Proyectar el nuevo estado
                Map<String, Object> state = Projection.project(events);

                return ResponseEntity.ok(events);
            } else if (result instanceof Failure<?> failure) {
                // Retornar el error como Failure
                return ResponseEntity.badRequest().body(failure);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(List.of(new ErrorEvent(e.getMessage())));
        }
        return null;
    }
}
