package org.example.ocrservice.listener;

import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.example.ocrservice.config.RabbitMQConfig;
import org.example.ocrservice.dto.DocumentMessage;
import org.example.ocrservice.dto.OCRResultMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OCRMessageListenerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private Tesseract tesseract;

    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private OCRMessageListener ocrMessageListener;

    @Test
    public void testReceiveMessage_Success() throws Exception {
        DocumentMessage message = new DocumentMessage(123L, "Test Title", "sample.pdf", "uuid_sample.pdf");
        String mockExtractedText = "This is some OCR text.";

        GetObjectResponse mockResponse = mock(GetObjectResponse.class);
        when(mockResponse.read(any(byte[].class))).thenAnswer(invocation -> {
            byte[] buf = invocation.getArgument(0);
            byte[] content = "fake pdf content".getBytes();
            System.arraycopy(content, 0, buf, 0, content.length);
            return content.length;
        }).thenReturn(-1);
        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(mockResponse);
        when(tesseract.doOCR(any(java.io.File.class))).thenReturn(mockExtractedText);

        ocrMessageListener.receiveMessage(message);

        ArgumentCaptor<OCRResultMessage> resultCaptor = ArgumentCaptor.forClass(OCRResultMessage.class);
        verify(rabbitTemplate, times(1)).convertAndSend(eq(RabbitMQConfig.OCR_RESULT_QUEUE), resultCaptor.capture());

        OCRResultMessage result = resultCaptor.getValue();
        assertNotNull(result);
        assertEquals(123L, result.getDocumentId());
        assertEquals(mockExtractedText, result.getExtractedText());
    }

    @Test
    public void testReceiveMessage_NullMessage() {
        ocrMessageListener.receiveMessage(null);
        verify(rabbitTemplate, never()).convertAndSend(anyString(), any(OCRResultMessage.class));
    }

    @Test
    public void testReceiveMessage_EmptyFilePath() {
        DocumentMessage message = new DocumentMessage(125L, "Empty Path", "empty.pdf", "");
        ocrMessageListener.receiveMessage(message);
        verify(rabbitTemplate, never()).convertAndSend(anyString(), any(OCRResultMessage.class));
    }

    @Test
    public void testReceiveMessage_NullFilePath() {
        DocumentMessage message = new DocumentMessage(126L, "Null Path", "null.pdf", null);
        ocrMessageListener.receiveMessage(message);
        verify(rabbitTemplate, never()).convertAndSend(anyString(), any(OCRResultMessage.class));
    }

    @Test
    public void testReceiveMessage_MinIOError() throws Exception {
        DocumentMessage message = new DocumentMessage(127L, "Error", "error.pdf", "uuid_error.pdf");
        when(minioClient.getObject(any(GetObjectArgs.class))).thenThrow(new RuntimeException("MinIO connection failed"));

        ocrMessageListener.receiveMessage(message);

        verify(rabbitTemplate, never()).convertAndSend(anyString(), any(OCRResultMessage.class));
    }

    @Test
    public void testReceiveMessage_TesseractException() throws Exception {
        DocumentMessage message = new DocumentMessage(128L, "OCR Fail", "fail.pdf", "uuid_fail.pdf");

        GetObjectResponse mockResponse = mock(GetObjectResponse.class);
        when(mockResponse.read(any(byte[].class))).thenReturn(-1);
        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(mockResponse);
        when(tesseract.doOCR(any(java.io.File.class))).thenThrow(new TesseractException("OCR Error"));

        ocrMessageListener.receiveMessage(message);

        verify(rabbitTemplate, never()).convertAndSend(anyString(), any(OCRResultMessage.class));
    }
}