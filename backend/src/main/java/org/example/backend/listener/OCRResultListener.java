package org.example.backend.listener;

import lombok.RequiredArgsConstructor;
import org.example.backend.config.RabbitMQConfig;
import org.example.backend.service.DocumentService;
import org.example.backend.service.dto.OCRResultMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OCRResultListener {

    private static final Logger logger = LoggerFactory.getLogger(OCRResultListener.class);

    private final DocumentService documentService;

    @RabbitListener(queues = RabbitMQConfig.OCR_RESULT_QUEUE)
    public void receiveOCRResult(OCRResultMessage message) {
        logger.info("Received OCR result for document ID: {}", message.getDocumentId());

        documentService.updateDocumentText(message.getDocumentId(), message.getExtractedText());
    }
}
