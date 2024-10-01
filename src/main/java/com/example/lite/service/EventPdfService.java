package com.example.lite.service;
import com.example.lite.repository.EventEntity;
import com.example.lite.repository.EventStoreRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class EventPdfService {

    private final EventStoreRepository eventStoreRepository;

    public EventPdfService(EventStoreRepository eventStoreRepository) {
        this.eventStoreRepository = eventStoreRepository;
    }

    // Generar el PDF de forma reactiva
    public Mono<ByteArrayInputStream> generatePdf(String aggregateId) {
        return eventStoreRepository.findByAggregateIdOrderByVersion(aggregateId)
                .collectList()
                .flatMap(this::createPdfFromEvents);
    }

    // Crear el PDF desde los eventos
    private Mono<ByteArrayInputStream> createPdfFromEvents(List<EventEntity> events) {
        return Mono.fromCallable(() -> {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try (PdfWriter writer = new PdfWriter(out);
                 PdfDocument pdfDoc = new PdfDocument(writer);
                 Document document = new Document(pdfDoc)) {
                events.forEach(event -> document.add(new Paragraph(
                        String.format("Event ID: %s, Type: %s, Data: %s, Version: %d, Created At: %s",
                                event.id(), event.eventType(), event.eventData(), event.version(), event.createdAt()))));
            }
            return new ByteArrayInputStream(out.toByteArray());
        });
    }
}


