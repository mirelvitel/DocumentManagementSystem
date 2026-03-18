package org.example.backend.service.implementation;

import org.example.backend.config.FileStorageProperties;
import org.example.backend.exception.DocumentException;
import org.example.backend.persistence.elasticsearch.DocumentSearchEntity;
import org.example.backend.persistence.elasticsearch.DocumentSearchRepository;
import org.example.backend.persistence.entity.DocumentEntity;
import org.example.backend.persistence.repository.DocumentRepository;
import org.example.backend.service.dto.DocumentDTO;
import org.example.backend.service.mapper.DocumentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceImplTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private FileStorageProperties fileStorageProperties;

    @Mock
    private DocumentMapper documentMapper;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private DocumentSearchRepository documentSearchRepository;

    @InjectMocks
    private DocumentServiceImpl documentService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        when(fileStorageProperties.getUploadDir()).thenReturn(tempDir.toString());
        documentService.init();
    }

    @Test
    void uploadDocument_shouldSaveAndSendMessage() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.pdf");
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream("content".getBytes()));

        DocumentEntity savedEntity = new DocumentEntity("test", "test.pdf", tempDir + "/test.pdf");
        savedEntity.setId(1L);
        when(documentRepository.save(any(DocumentEntity.class))).thenReturn(savedEntity);

        DocumentDTO expectedDTO = new DocumentDTO(1L, "test", "test.pdf", null);
        when(documentMapper.toDTO(savedEntity)).thenReturn(expectedDTO);

        DocumentDTO result = documentService.uploadDocument(file);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("test", result.getTitle());
        verify(documentRepository).save(any(DocumentEntity.class));
        verify(rabbitTemplate).convertAndSend(anyString(), any());
    }

    @Test
    void getAllDocuments_shouldReturnMappedDTOs() {
        DocumentEntity entity1 = new DocumentEntity("Doc1", "doc1.pdf", "/path/doc1.pdf");
        entity1.setId(1L);
        DocumentEntity entity2 = new DocumentEntity("Doc2", "doc2.pdf", "/path/doc2.pdf");
        entity2.setId(2L);

        when(documentRepository.findAll()).thenReturn(List.of(entity1, entity2));
        when(documentMapper.toDTO(entity1)).thenReturn(new DocumentDTO(1L, "Doc1", "doc1.pdf", null));
        when(documentMapper.toDTO(entity2)).thenReturn(new DocumentDTO(2L, "Doc2", "doc2.pdf", null));

        List<DocumentDTO> result = documentService.getAllDocuments();

        assertEquals(2, result.size());
        verify(documentRepository).findAll();
    }

    @Test
    void getDocumentById_existingId_shouldReturnDTO() {
        DocumentEntity entity = new DocumentEntity("Test", "test.pdf", "/path/test.pdf");
        entity.setId(1L);
        when(documentRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(documentMapper.toDTO(entity)).thenReturn(new DocumentDTO(1L, "Test", "test.pdf", null));

        DocumentDTO result = documentService.getDocumentById(1L);

        assertNotNull(result);
        assertEquals("Test", result.getTitle());
    }

    @Test
    void getDocumentById_nonExistingId_shouldThrow() {
        when(documentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(DocumentException.class, () -> documentService.getDocumentById(999L));
    }

    @Test
    void deleteDocumentById_shouldDeleteFromDbAndElasticsearch() throws IOException {
        DocumentEntity entity = new DocumentEntity("Test", "test.pdf", tempDir + "/test.pdf");
        entity.setId(1L);
        when(documentRepository.findById(1L)).thenReturn(Optional.of(entity));

        documentService.deleteDocumentById(1L);

        verify(documentRepository).delete(entity);
        verify(documentSearchRepository).deleteDocument("1");
    }

    @Test
    void deleteDocumentById_nonExistingId_shouldThrow() {
        when(documentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(DocumentException.class, () -> documentService.deleteDocumentById(999L));
    }

    @Test
    void updateDocumentText_shouldSaveAndIndex() throws IOException {
        DocumentEntity entity = new DocumentEntity("Test", "test.pdf", "/path/test.pdf");
        entity.setId(1L);
        when(documentRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(documentRepository.save(entity)).thenReturn(entity);
        when(documentMapper.toSearchEntity(entity)).thenReturn(new DocumentSearchEntity());

        documentService.updateDocumentText(1L, "Extracted text content");

        assertEquals("Extracted text content", entity.getTextContent());
        verify(documentRepository).save(entity);
        verify(documentSearchRepository).indexDocument(any(DocumentSearchEntity.class));
    }

    @Test
    void searchDocuments_shouldReturnMappedResults() throws IOException {
        DocumentSearchEntity searchEntity = new DocumentSearchEntity(1L, "Test", "test.pdf", "/path", "content");
        when(documentSearchRepository.searchByContent("test")).thenReturn(List.of(searchEntity));
        when(documentMapper.searchEntityToDTO(searchEntity)).thenReturn(new DocumentDTO(1L, "Test", "test.pdf", "content"));

        List<DocumentDTO> results = documentService.searchDocuments("test");

        assertEquals(1, results.size());
        assertEquals("Test", results.get(0).getTitle());
    }

    @Test
    void uploadDocument_withPathTraversal_shouldThrow() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("../etc/passwd");

        assertThrows(DocumentException.class, () -> documentService.uploadDocument(file));
    }
}