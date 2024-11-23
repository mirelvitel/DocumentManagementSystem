package org.example.backend.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class OCRResultMessage implements Serializable {
    private Long documentId;
    private String extractedText;

    public OCRResultMessage() {}

    public OCRResultMessage(Long documentId, String extractedText) {
        this.documentId = documentId;
        this.extractedText = extractedText;
    }

}
