package org.example.backend.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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

    public DocumentEntity() {}

    public DocumentEntity(String title, String fileName, String filePath) {
        this.title = title;
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
