package org.example.backend.api;

import lombok.RequiredArgsConstructor;
import org.example.backend.service.DocumentService;
import org.example.backend.service.dto.DocumentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class DocumentController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);
    private final DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<DocumentDTO> uploadDocument(@RequestParam("file") MultipartFile file) {
        logger.info("Upload request for file: {}", file.getOriginalFilename());
        DocumentDTO documentDTO = documentService.uploadDocument(file);
        return new ResponseEntity<>(documentDTO, HttpStatus.CREATED);
    }

    @GetMapping("/documents")
    public ResponseEntity<List<DocumentDTO>> getAllDocuments() {
        List<DocumentDTO> documents = documentService.getAllDocuments();
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/documents/{id}")
    public ResponseEntity<DocumentDTO> getDocumentById(@PathVariable Long id) {
        DocumentDTO document = documentService.getDocumentById(id);
        return ResponseEntity.ok(document);
    }

    @GetMapping("/documents/download/{id}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) {
        logger.info("Download request for document ID: {}", id);
        DocumentDTO documentDTO = documentService.getDocumentById(id);
        InputStream inputStream = documentService.getDocumentFile(id);

        InputStreamResource resource = new InputStreamResource(inputStream);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + documentDTO.getFileName() + "\"")
                .body(resource);
    }

    @DeleteMapping("/documents/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        logger.info("Delete request for document ID: {}", id);
        documentService.deleteDocumentById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/documents/search")
    public ResponseEntity<List<DocumentDTO>> searchDocuments(@RequestParam String keyword) {
        logger.info("Search request for keyword: {}", keyword);
        List<DocumentDTO> searchResults = documentService.searchDocuments(keyword);
        return ResponseEntity.ok(searchResults);
    }
}