package com.example.lite.service;

import java.nio.ByteBuffer;

import org.springframework.stereotype.Service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;

import jakarta.mail.util.ByteArrayDataSource;
import reactor.core.publisher.Mono;

@Service
public class PDFService {

    private final AmazonSimpleEmailService sesClient;

    public PDFService(AmazonSimpleEmailService sesClient) {
        this.sesClient = sesClient;
    }

    public byte[] generatePdf() {
        // Aquí se debe generar el contenido del PDF
        return "PDF Content".getBytes();
    }

    public Mono<Void> sendPdfByEmail(String email) {
        byte[] pdfContent = generatePdf();
        ByteArrayDataSource dataSource = new ByteArrayDataSource(pdfContent, "application/pdf");
        try {
            SendRawEmailRequest emailRequest = new SendRawEmailRequest()
                    .withRawMessage(new RawMessage()
                            .withData(ByteBuffer.wrap(createEmailWithAttachment(email, dataSource))));
            sesClient.sendRawEmail(emailRequest);
            return Mono.empty();
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    private byte[] createEmailWithAttachment(String email, ByteArrayDataSource dataSource) throws Exception {
        // Aquí se construye el correo con el archivo adjunto
        return "Email content".getBytes();
    }
}