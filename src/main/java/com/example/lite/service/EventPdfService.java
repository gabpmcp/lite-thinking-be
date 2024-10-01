package com.example.lite.service;
import com.example.lite.repository.EventEntity;
import com.example.lite.repository.EventStoreRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import jakarta.mail.*;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;

import java.io.ByteArrayInputStream;
import java.util.Properties;

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

    // Enviar PDF por correo utilizando Amazon SES
    public void sendPdfByEmail(String recipientEmail, ByteArrayInputStream pdfStream) throws MessagingException, IOException {
        String senderEmail = "gabrielalejandrobernalperez@gmail.com";  // Cambia esto por tu correo verificado en SES

        // Crear sesión de correo
        Properties props = new Properties();
        props.put("mail.smtp.host", System.getProperty("SMTP_HOST", "")); // Cambia por tu región
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port", System.getProperty("SMTP_PORT", ""));

        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(System.getProperty("SMTP_USER_NAME", ""), System.getProperty("SMTP_PASSWORD", "")); // Reemplaza con tus credenciales SMTP de SES
            }
        };

        Session session = Session.getInstance(props, auth);

        // Crear mensaje
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderEmail));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
        message.setSubject("Here is your PDF");

        // Crear el contenido del correo con el adjunto
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText("Please find the attached PDF.");

        MimeBodyPart attachmentPart = new MimeBodyPart();
        DataSource source = new ByteArrayDataSource(pdfStream, "application/pdf");
        attachmentPart.setDataHandler(new DataHandler(source));
        attachmentPart.setFileName("events.pdf");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        multipart.addBodyPart(attachmentPart);
        message.setContent(multipart);

        // Enviar correo usando SES
        Transport.send(message);
    }
}


