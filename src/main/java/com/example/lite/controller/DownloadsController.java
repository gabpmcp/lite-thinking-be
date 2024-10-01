package com.example.lite.controller;

import com.example.lite.service.EventPdfService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

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
}
