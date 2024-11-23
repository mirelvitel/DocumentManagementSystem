package org.example.ocrservice.listener;

import org.example.ocrservice.config.RabbitMQConfig;
import org.example.ocrservice.dto.DocumentMessage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.File;

public class OCRMessageListenerTest {

    @Test
    public void testReceiveMessage() {
        // Mock the RabbitTemplate
        RabbitTemplate rabbitTemplate = Mockito.mock(RabbitTemplate.class);

        // Create an instance of the listener
        OCRMessageListener listener = new OCRMessageListener(rabbitTemplate);

        // Create a sample DocumentMessage
        DocumentMessage message = new DocumentMessage();
        message.setDocumentId(1L);
        message.setFilePath("src/test/resources/test-pdf.pdf");

        // Create a sample PDF file
        File pdfFile = new File(message.getFilePath());
        // Ensure the file exists for the test

        // Invoke the method
        listener.receiveMessage(message);

        // Verify that the OCR result was sent
        Mockito.verify(rabbitTemplate, Mockito.times(1))
                .convertAndSend(Mockito.eq(RabbitMQConfig.OCR_RESULT_QUEUE), Mockito.any(String.class));
    }

}
