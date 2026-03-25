package org.example.ocrservice.listener;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Component
@RequiredArgsConstructor
public class OCRMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(OCRMessageListener.class);

    private final RabbitTemplate rabbitTemplate;
    private final Tesseract tesseract;
    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @RabbitListener(queues = RabbitMQConfig.DOCUMENT_UPLOAD_QUEUE)
    public void receiveMessage(DocumentMessage message) {
        if (message == null) {
            logger.error("Received null DocumentMessage. Skipping processing.");
            return;
        }

        logger.info("Received message: {}", message);

        if (message.getFilePath() == null || message.getFilePath().trim().isEmpty()) {
            logger.error("File path (object key) is null or empty for DocumentMessage: {}", message);
            return;
        }

        File tempFile = null;
        try {
            // Download file from MinIO to a temp file
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(message.getFilePath())
                            .build()
            );

            tempFile = File.createTempFile("ocr_", "_" + message.getFileName());
            Files.copy(stream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            stream.close();

            String extractedText = tesseract.doOCR(tempFile);

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
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
}