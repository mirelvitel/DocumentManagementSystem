package org.example.backend.service.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DocumentDTO {

    private Long id;
    private String title;
    private String fileName;
    private String filePath;
    private String textContent;

    public DocumentDTO() {}

    public DocumentDTO(Long id, String title, String fileName, String filePath, String textContent) {
        this.id = id;
        this.title = title;
        this.fileName = fileName;
        this.filePath = filePath;
        this.textContent = textContent;
    }

}
