package org.example.backend.persistence.repository;

import org.example.backend.persistence.entity.DocumentEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DocumentRepositoryTest {

    @Mock
    private DocumentRepository repository;

    @InjectMocks
    private DocumentRepositoryTest documentRepositoryTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveAndFindById() {
        // Arrange: Create a mock document entity
        DocumentEntity entity = new DocumentEntity("Title", "file.pdf", "/path/file.pdf");
        when(repository.save(entity)).thenReturn(entity);
        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));

        // Act: Save the entity and retrieve it
        DocumentEntity savedEntity = repository.save(entity);
        Optional<DocumentEntity> foundEntity = repository.findById(savedEntity.getId());

        // Assert: Verify that the entity was saved and retrieved
        assertNotNull(savedEntity);
        assertTrue(foundEntity.isPresent());
        assertEquals("Title", foundEntity.get().getTitle());
    }

    @Test
    void testFindByTitleContainingIgnoreCase() {
        // Arrange: Create mock documents and simulate a search
        DocumentEntity entity1 = new DocumentEntity("Test Document", "file1.pdf", "/path/file1.pdf");
        DocumentEntity entity2 = new DocumentEntity("Another Document", "file2.pdf", "/path/file2.pdf");
        List<DocumentEntity> searchResults = new ArrayList<>();
        searchResults.add(entity1);
        when(repository.findByTitleContainingIgnoreCase("test")).thenReturn(searchResults);

        // Act: Perform the search
        List<DocumentEntity> results = repository.findByTitleContainingIgnoreCase("test");

        // Assert: Verify the search results
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("Test Document", results.get(0).getTitle());
    }

    @Test
    void testDeleteDocument() {
        // Arrange: Create a mock document and simulate deletion
        DocumentEntity entity = new DocumentEntity("Title", "file.pdf", "/path/file.pdf");
        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));
        doNothing().when(repository).delete(entity);

        // Act: Delete the document
        repository.delete(entity);
        when(repository.findById(entity.getId())).thenReturn(Optional.empty());

        // Assert: Verify the document was deleted
        Optional<DocumentEntity> deletedEntity = repository.findById(entity.getId());
        assertFalse(deletedEntity.isPresent());
    }
}
