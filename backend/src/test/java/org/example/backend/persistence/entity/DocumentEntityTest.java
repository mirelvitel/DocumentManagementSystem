package org.example.backend.persistence.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DocumentEntityTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validEntity_shouldHaveNoViolations() {
        DocumentEntity entity = new DocumentEntity("My Document", "file.pdf", "/uploads/file.pdf");
        Set<ConstraintViolation<DocumentEntity>> violations = validator.validate(entity);
        assertTrue(violations.isEmpty());
    }

    @Test
    void blankTitle_shouldHaveViolation() {
        DocumentEntity entity = new DocumentEntity("", "file.pdf", "/uploads/file.pdf");
        Set<ConstraintViolation<DocumentEntity>> violations = validator.validate(entity);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("title")));
    }

    @Test
    void blankFileName_shouldHaveViolation() {
        DocumentEntity entity = new DocumentEntity("Title", "", "/uploads/file.pdf");
        Set<ConstraintViolation<DocumentEntity>> violations = validator.validate(entity);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("fileName")));
    }

    @Test
    void blankFilePath_shouldHaveViolation() {
        DocumentEntity entity = new DocumentEntity("Title", "file.pdf", "");
        Set<ConstraintViolation<DocumentEntity>> violations = validator.validate(entity);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("filePath")));
    }

    @Test
    void titleExceedingMaxLength_shouldHaveViolation() {
        String longTitle = "a".repeat(256);
        DocumentEntity entity = new DocumentEntity(longTitle, "file.pdf", "/uploads/file.pdf");
        Set<ConstraintViolation<DocumentEntity>> violations = validator.validate(entity);
        assertFalse(violations.isEmpty());
    }

    @Test
    void defaultConstructor_shouldCreateEmptyEntity() {
        DocumentEntity entity = new DocumentEntity();
        assertNull(entity.getId());
        assertNull(entity.getTitle());
        assertNull(entity.getFileName());
        assertNull(entity.getFilePath());
        assertNull(entity.getTextContent());
    }

    @Test
    void settersAndGetters_shouldWork() {
        DocumentEntity entity = new DocumentEntity();
        entity.setId(1L);
        entity.setTitle("Test");
        entity.setFileName("test.pdf");
        entity.setFilePath("/uploads/test.pdf");
        entity.setTextContent("Extracted text");

        assertEquals(1L, entity.getId());
        assertEquals("Test", entity.getTitle());
        assertEquals("test.pdf", entity.getFileName());
        assertEquals("/uploads/test.pdf", entity.getFilePath());
        assertEquals("Extracted text", entity.getTextContent());
    }
}