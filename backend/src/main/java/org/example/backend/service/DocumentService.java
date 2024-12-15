package org.example.backend.service;

import org.example.backend.service.dto.DocumentDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {
    DocumentDTO uploadDocument(MultipartFile file);
    List<DocumentDTO> getAllDocuments();
    void deleteDocumentById(Long id);
    DocumentDTO getDocumentById(Long id);
    void updateDocumentText(Long documentId, String extractedText);
    List<DocumentDTO> searchDocuments(String keyword);
}
