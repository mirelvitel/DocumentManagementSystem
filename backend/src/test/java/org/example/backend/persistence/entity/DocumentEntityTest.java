package org.example.backend.persistence.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DocumentEntityTest {

    @Mock
    private Validator validator;

    @InjectMocks
    private DocumentEntityTest documentEntityTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidDocumentEntity() {
        // Arrange: Create a valid document entity and mock no violations
        DocumentEntity entity = new DocumentEntity("Valid Title", "file.pdf", "/path/file.pdf");
        when(validator.validate(entity)).thenReturn(Collections.emptySet());

        // Act: Validate the entity
        Set<ConstraintViolation<DocumentEntity>> violations = validator.validate(entity);

        // Assert: Ensure no violations
        assertTrue(violations.isEmpty());
        verify(validator, times(1)).validate(entity);
    }

    @Test
    void testBlankTitle() {
        // Arrange: Create a document entity with a blank title and mock a violation
        DocumentEntity entity = new DocumentEntity("", "file.pdf", "/path/file.pdf");
        ConstraintViolation<DocumentEntity> violation = mock(ConstraintViolation.class);
        when(validator.validate(entity)).thenReturn(Set.of(violation));

        // Act: Validate the entity
        Set<ConstraintViolation<DocumentEntity>> violations = validator.validate(entity);

        // Assert: Ensure one violation is present
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        verify(validator, times(1)).validate(entity);
    }
}
