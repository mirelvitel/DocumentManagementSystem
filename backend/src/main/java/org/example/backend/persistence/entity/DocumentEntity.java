package org.example.backend.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "documents")
public class DocumentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title must not be empty")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "File name must not be empty")
    @Size(max = 255, message = "File name must not exceed 255 characters")
    private String fileName;

    @NotBlank(message = "File path must not be empty")
    @Size(max = 500, message = "File path must not exceed 500 characters")
    private String filePath;

    @Column(columnDefinition = "TEXT")
    private String textContent;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private OcrStatus ocrStatus = OcrStatus.PENDING;

    public DocumentEntity() {}

    public DocumentEntity(String title, String fileName, String filePath) {
        this.title = title;
        this.fileName = fileName;
        this.filePath = filePath;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.ocrStatus == null) {
            this.ocrStatus = OcrStatus.PENDING;
        }
    }

    public enum OcrStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED
    }
}