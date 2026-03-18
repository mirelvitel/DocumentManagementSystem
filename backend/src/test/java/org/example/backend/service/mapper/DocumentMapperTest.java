package org.example.backend.service.mapper;

import org.example.backend.persistence.elasticsearch.DocumentSearchEntity;
import org.example.backend.persistence.entity.DocumentEntity;
import org.example.backend.service.dto.DocumentDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class DocumentMapperTest {

    private final DocumentMapper mapper = Mappers.getMapper(DocumentMapper.class);

    @Test
    void toDTO_shouldMapFieldsAndIgnoreFilePath() {
        DocumentEntity entity = new DocumentEntity("Title", "file.pdf", "/server/path/file.pdf");
        entity.setId(1L);
        entity.setTextContent("OCR text");

        DocumentDTO dto = mapper.toDTO(entity);

        assertEquals(1L, dto.getId());
        assertEquals("Title", dto.getTitle());
        assertEquals("file.pdf", dto.getFileName());
        assertEquals("OCR text", dto.getTextContent());
    }

    @Test
    void toDTO_withNullTextContent_shouldMapCorrectly() {
        DocumentEntity entity = new DocumentEntity("Title", "file.pdf", "/server/path/file.pdf");
        entity.setId(2L);

        DocumentDTO dto = mapper.toDTO(entity);

        assertEquals(2L, dto.getId());
        assertNull(dto.getTextContent());
    }

    @Test
    void toSearchEntity_shouldMapAllFields() {
        DocumentEntity entity = new DocumentEntity("Title", "file.pdf", "/path/file.pdf");
        entity.setId(1L);
        entity.setTextContent("OCR text");

        DocumentSearchEntity searchEntity = mapper.toSearchEntity(entity);

        assertEquals(1L, searchEntity.getId());
        assertEquals("Title", searchEntity.getTitle());
        assertEquals("file.pdf", searchEntity.getFileName());
        assertEquals("/path/file.pdf", searchEntity.getFilePath());
        assertEquals("OCR text", searchEntity.getTextContent());
    }

    @Test
    void searchEntityToDTO_shouldMapFieldsAndIgnoreFilePath() {
        DocumentSearchEntity searchEntity = new DocumentSearchEntity(1L, "Title", "file.pdf", "/path/file.pdf", "OCR text");

        DocumentDTO dto = mapper.searchEntityToDTO(searchEntity);

        assertEquals(1L, dto.getId());
        assertEquals("Title", dto.getTitle());
        assertEquals("file.pdf", dto.getFileName());
        assertEquals("OCR text", dto.getTextContent());
    }

    @Test
    void toEntity_fromSearchEntity_shouldMapAllFields() {
        DocumentSearchEntity searchEntity = new DocumentSearchEntity(1L, "Title", "file.pdf", "/path/file.pdf", "OCR text");

        DocumentEntity entity = mapper.toEntity(searchEntity);

        assertEquals(1L, entity.getId());
        assertEquals("Title", entity.getTitle());
        assertEquals("file.pdf", entity.getFileName());
        assertEquals("/path/file.pdf", entity.getFilePath());
        assertEquals("OCR text", entity.getTextContent());
    }
}