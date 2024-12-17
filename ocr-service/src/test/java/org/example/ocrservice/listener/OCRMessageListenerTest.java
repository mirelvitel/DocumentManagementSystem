package org.example.ocrservice.listener;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.example.ocrservice.config.RabbitMQConfig;
import org.example.ocrservice.dto.DocumentMessage;
import org.example.ocrservice.dto.OCRResultMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OCRMessageListenerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private Tesseract tesseract;

    @InjectMocks
    private OCRMessageListener ocrMessageListener;

    @TempDir
    Path tempDir;

    private File pdfFile;
    private DocumentMessage documentMessage;

    @BeforeEach
    public void setUp() throws Exception {
        // Create a temporary file to simulate a PDF
        pdfFile = Files.createFile(tempDir.resolve("sample.pdf")).toFile();
        documentMessage = new DocumentMessage(123L, "Test Title", "sample.pdf", pdfFile.getAbsolutePath());
    }

    @Test
    public void testReceiveMessage_Success() throws Exception {
        // Given
        String mockExtractedText = "This is some OCR text.";
        when(tesseract.doOCR(pdfFile)).thenReturn(mockExtractedText);

        // When
        ocrMessageListener.receiveMessage(documentMessage);

        // Then
        ArgumentCaptor<OCRResultMessage> resultCaptor = ArgumentCaptor.forClass(OCRResultMessage.class);
        verify(rabbitTemplate, times(1)).convertAndSend(eq(RabbitMQConfig.OCR_RESULT_QUEUE), resultCaptor.capture());

        OCRResultMessage result = resultCaptor.getValue();
        assertNotNull(result, "OCRResultMessage should not be null");
        assertEquals(documentMessage.getDocumentId(), result.getDocumentId(), "Document IDs should match");
        assertEquals(mockExtractedText, result.getExtractedText(), "Extracted text should match the mock");
    }

    @Test
    public void testReceiveMessage_FileNotFound() {
        // Given a non-existent file path
        DocumentMessage nonExistentMessage = new DocumentMessage(124L, "NonExistent", "no_file.pdf", "non_existent.pdf");

        // When
        ocrMessageListener.receiveMessage(nonExistentMessage);

        // Then
        verify(rabbitTemplate, never()).convertAndSend(anyString(), any(OCRResultMessage.class));
    }

    @Test
    public void testReceiveMessage_TesseractException() throws Exception {
        // Given a Tesseract exception
        when(tesseract.doOCR(pdfFile)).thenThrow(new TesseractException("OCR Error"));

        // When
        ocrMessageListener.receiveMessage(documentMessage);

        // Then
        verify(rabbitTemplate, never()).convertAndSend(anyString(), any(OCRResultMessage.class));
    }

    @Test
    public void testReceiveMessage_NullMessage() {
        // When
        ocrMessageListener.receiveMessage(null);

        // Then
        verify(rabbitTemplate, never()).convertAndSend(anyString(), any(OCRResultMessage.class));
        // Optionally, verify that an error was logged about the null message
    }

    @Test
    public void testReceiveMessage_EmptyFilePath() {
        // Given a DocumentMessage with an empty file path
        DocumentMessage emptyPathMessage = new DocumentMessage(125L, "Empty Path", "empty.pdf", "");

        // When
        ocrMessageListener.receiveMessage(emptyPathMessage);

        // Then
        verify(rabbitTemplate, never()).convertAndSend(anyString(), any(OCRResultMessage.class));
        // Optionally, verify that an error was logged about the empty file path
    }

    @Test
    public void testReceiveMessage_RabbitTemplateException() throws Exception {
        // Given
        String mockExtractedText = "This is some OCR text.";
        when(tesseract.doOCR(pdfFile)).thenReturn(mockExtractedText);
        doThrow(new RuntimeException("RabbitMQ Error")).when(rabbitTemplate).convertAndSend(eq(RabbitMQConfig.OCR_RESULT_QUEUE), any(OCRResultMessage.class));

        // When
        ocrMessageListener.receiveMessage(documentMessage);

        // Then
        verify(rabbitTemplate, times(1)).convertAndSend(eq(RabbitMQConfig.OCR_RESULT_QUEUE), any(OCRResultMessage.class));
        // Optionally, verify that an error was logged about the RabbitMQ failure
    }

    @Test
    public void testReceiveMessage_MalformedMessage() {
        // Given a DocumentMessage with missing file path
        DocumentMessage malformedMessage = new DocumentMessage(126L, "Malformed", "malformed.pdf", null);

        // When
        ocrMessageListener.receiveMessage(malformedMessage);

        // Then
        verify(rabbitTemplate, never()).convertAndSend(anyString(), any(OCRResultMessage.class));
        // Optionally, verify that an error was logged about the malformed message
    }
}
