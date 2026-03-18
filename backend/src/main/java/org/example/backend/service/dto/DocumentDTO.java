package org.example.backend.service.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DocumentDTO {

    private Long id;
    private String title;
    private String fileName;
    private String textContent;

    public DocumentDTO() {}

    public DocumentDTO(Long id, String title, String fileName, String textContent) {
        this.id = id;
        this.title = title;
        this.fileName = fileName;
        this.textContent = textContent;
    }
}