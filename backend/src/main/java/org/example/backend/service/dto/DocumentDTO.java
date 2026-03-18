package org.example.backend.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class DocumentDTO {

    private Long id;
    private String title;
    private String fileName;
    private String textContent;
    private LocalDateTime createdAt;
    private String ocrStatus;

    public DocumentDTO() {}

    public DocumentDTO(Long id, String title, String fileName, String textContent) {
        this.id = id;
        this.title = title;
        this.fileName = fileName;
        this.textContent = textContent;
    }
}