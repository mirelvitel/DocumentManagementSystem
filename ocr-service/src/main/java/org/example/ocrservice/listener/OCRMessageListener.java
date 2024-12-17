package org.example.ocrservice.listener;

import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.example.ocrservice.config.RabbitMQConfig;
import org.example.ocrservice.dto.DocumentMessage;
import org.example.ocrservice.dto.OCRResultMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@RequiredArgsConstructor
public class OCRMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(OCRMessageListener.class);

    private final RabbitTemplate rabbitTemplate;
    private final Tesseract tesseract; // Injected via constructor

    @RabbitListener(queues = RabbitMQConfig.DOCUMENT_UPLOAD_QUEUE)
    public void receiveMessage(DocumentMessage message) {
        // Null check for the incoming message
        if (message == null) {
            logger.error("Received null DocumentMessage. Skipping processing.");
            return;
        }

        logger.info("Received message: {}", message);

        try {
            // Null and empty check for filePath
            if (message.getFilePath() == null || message.getFilePath().trim().isEmpty()) {
                logger.error("File path is null or empty for DocumentMessage: {}", message);
                return;
            }

            File pdfFile = new File(message.getFilePath());
            if (!pdfFile.exists()) {
                logger.error("File not found: {}", message.getFilePath());
                return;
            }

            String extractedText = tesseract.doOCR(pdfFile);

            OCRResultMessage resultMessage = new OCRResultMessage(
                    message.getDocumentId(),
                    extractedText
            );

            rabbitTemplate.convertAndSend(RabbitMQConfig.OCR_RESULT_QUEUE, resultMessage);
            logger.info("Sent OCR result for document ID: {}", message.getDocumentId());

        } catch (TesseractException e) {
            logger.error("OCR processing failed for document ID: {}", message.getDocumentId(), e);
        } catch (Exception e) {
            logger.error("An error occurred while processing document ID: {}", message.getDocumentId(), e);
        }
    }
}
