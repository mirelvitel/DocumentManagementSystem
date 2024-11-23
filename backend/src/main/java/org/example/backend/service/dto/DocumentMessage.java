package org.example.backend.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class DocumentMessage implements Serializable {
    private Long documentId;
    private String title;
    private String fileName;
    private String filePath;

    public DocumentMessage() {}

    public DocumentMessage(Long documentId, String title, String fileName, String filePath) {
        this.documentId = documentId;
        this.title = title;
        this.fileName = fileName;
        this.filePath = filePath;
    }

}
