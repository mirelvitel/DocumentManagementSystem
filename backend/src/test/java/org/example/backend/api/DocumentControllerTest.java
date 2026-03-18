package org.example.backend.api;

import org.example.backend.exception.DocumentException;
import org.example.backend.exception.GlobalExceptionHandler;
import org.example.backend.service.DocumentService;
import org.example.backend.service.dto.DocumentDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DocumentControllerTest {

    @Mock
    private DocumentService documentService;

    @InjectMocks
    private DocumentController documentController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(documentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void uploadDocument_shouldReturn201() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "content".getBytes());
        DocumentDTO dto = new DocumentDTO(1L, "test", "test.pdf", null);
        when(documentService.uploadDocument(any())).thenReturn(dto);

        mockMvc.perform(multipart("/api/upload").file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("test"));
    }

    @Test
    void getAllDocuments_shouldReturnList() throws Exception {
        List<DocumentDTO> docs = List.of(
                new DocumentDTO(1L, "Doc1", "doc1.pdf", null),
                new DocumentDTO(2L, "Doc2", "doc2.pdf", null)
        );
        when(documentService.getAllDocuments()).thenReturn(docs);

        mockMvc.perform(get("/api/documents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Doc1"));
    }

    @Test
    void getDocumentById_existing_shouldReturn200() throws Exception {
        DocumentDTO dto = new DocumentDTO(1L, "Test", "test.pdf", "extracted text");
        when(documentService.getDocumentById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/documents/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test"))
                .andExpect(jsonPath("$.textContent").value("extracted text"));
    }

    @Test
    void getDocumentById_nonExisting_shouldReturn400() throws Exception {
        when(documentService.getDocumentById(999L)).thenThrow(new DocumentException("Document not found"));

        mockMvc.perform(get("/api/documents/999"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteDocument_shouldReturn204() throws Exception {
        doNothing().when(documentService).deleteDocumentById(1L);

        mockMvc.perform(delete("/api/documents/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void searchDocuments_shouldReturnResults() throws Exception {
        List<DocumentDTO> results = List.of(new DocumentDTO(1L, "Test", "test.pdf", "matching content"));
        when(documentService.searchDocuments("test")).thenReturn(results);

        mockMvc.perform(get("/api/documents/search").param("keyword", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].textContent").value("matching content"));
    }

    @Test
    void searchDocuments_emptyResults_shouldReturnEmptyList() throws Exception {
        when(documentService.searchDocuments("nonexistent")).thenReturn(List.of());

        mockMvc.perform(get("/api/documents/search").param("keyword", "nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}