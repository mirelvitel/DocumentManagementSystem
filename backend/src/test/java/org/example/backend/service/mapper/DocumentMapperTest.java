package org.example.backend.service.mapper;

import org.example.backend.persistence.elasticsearch.DocumentSearchEntity;
import org.example.backend.persistence.entity.DocumentEntity;
import org.example.backend.service.dto.DocumentDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DocumentMapperTest {

    @Mock
    private DocumentMapper mapper;

    @InjectMocks
    private DocumentMapperTest documentMapperTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testToDTO() {
        // Arrange: Create a mock DocumentEntity and a mapped DocumentDTO
        DocumentEntity entity = new DocumentEntity("Title", "file.pdf", "/path/file.pdf");
        entity.setId(1L);
        entity.setTextContent("Extracted content");
        DocumentDTO expectedDto = new DocumentDTO(1L, "Title", "file.pdf", "/path/file.pdf", "Extracted content");

        when(mapper.toDTO(entity)).thenReturn(expectedDto);

        // Act: Perform the mapping
        DocumentDTO actualDto = mapper.toDTO(entity);

        // Assert: Verify the mapped DTO matches the expected DTO
        assertEquals(expectedDto, actualDto);
        verify(mapper, times(1)).toDTO(entity);
    }

    @Test
    void testToEntity() {
        // Arrange: Create a mock DocumentDTO and a mapped DocumentEntity
        DocumentDTO dto = new DocumentDTO(1L, "Title", "file.pdf", "/path/file.pdf", "Extracted content");
        DocumentEntity expectedEntity = new DocumentEntity("Title", "file.pdf", "/path/file.pdf");
        expectedEntity.setId(1L);
        expectedEntity.setTextContent("Extracted content");

        when(mapper.toEntity(dto)).thenReturn(expectedEntity);

        // Act: Perform the mapping
        DocumentEntity actualEntity = mapper.toEntity(dto);

        // Assert: Verify the mapped Entity matches the expected Entity
        assertEquals(expectedEntity, actualEntity);
        verify(mapper, times(1)).toEntity(dto);
    }

    @Test
    void testToSearchEntity() {
        // Arrange: Create a mock DocumentEntity and a mapped DocumentSearchEntity
        DocumentEntity entity = new DocumentEntity("Title", "file.pdf", "/path/file.pdf");
        entity.setId(1L);
        entity.setTextContent("Extracted content");
        DocumentSearchEntity expectedSearchEntity = new DocumentSearchEntity(1L, "Title", "file.pdf", "/path/file.pdf", "Extracted content");

        when(mapper.toSearchEntity(entity)).thenReturn(expectedSearchEntity);

        // Act: Perform the mapping
        DocumentSearchEntity actualSearchEntity = mapper.toSearchEntity(entity);

        // Assert: Verify the mapped SearchEntity matches the expected SearchEntity
        assertEquals(expectedSearchEntity, actualSearchEntity);
        verify(mapper, times(1)).toSearchEntity(entity);
    }
}
