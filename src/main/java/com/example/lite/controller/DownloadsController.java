package com.example.lite.controller;

import com.example.lite.service.EventPdfService;
import jakarta.mail.MessagingException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/projections")
public class DownloadsController {
    private final EventPdfService eventPdfService;

    public DownloadsController(EventPdfService eventPdfService) {
        this.eventPdfService = eventPdfService;
    }

    // Endpoint para descargar el PDF
    @GetMapping("/download-pdf")
    public Mono<ResponseEntity<InputStreamResource>> downloadPdf(@RequestParam("aggregateId") String aggregateId) {
        return eventPdfService.generatePdf(aggregateId)
                .map(pdfStream -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=events.pdf")
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(new InputStreamResource(pdfStream)));
    }

    @PostMapping("/send-pdf")
    public Mono<ResponseEntity<String>> sendPdfByEmail(@RequestParam("aggregateId") String aggregateId, @RequestBody Map<String, String> email) {
        return eventPdfService.generatePdf(aggregateId)
                .flatMap(pdfStream -> {
                    try {
                        eventPdfService.sendPdfByEmail(email.getOrDefault("email", ""), pdfStream);
                        return Mono.just(ResponseEntity.ok("PDF sent to " + email));
                    } catch (Exception e) {
                        return Mono.just(ResponseEntity.status(500).body("Failed to send PDF: " + e.getMessage()));
                    }
                });
    }
}
