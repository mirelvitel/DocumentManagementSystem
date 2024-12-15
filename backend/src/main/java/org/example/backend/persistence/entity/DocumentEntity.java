package org.example.backend.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

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

    @Lob
    private String textContent;

    public DocumentEntity() {}

    public DocumentEntity(String title, String fileName, String filePath) {
        this.title = title;
        this.fileName = fileName;
        this.filePath = filePath;
    }
}
