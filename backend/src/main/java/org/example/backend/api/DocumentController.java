package org.example.backend.api;


import org.example.backend.service.dto.DocumentDTO;
import org.example.backend.exception.DocumentException;
import org.example.backend.service.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.MalformedURLException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@RestController
@RequestMapping("/api")
public class DocumentController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);
    private final DocumentService documentService;

    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<DocumentDTO> uploadDocument(@RequestParam("file") MultipartFile file) {
        DocumentDTO documentDTO = documentService.uploadDocument(file);
        return new ResponseEntity<>(documentDTO, HttpStatus.CREATED);
    }

    @GetMapping("/documents")
    public ResponseEntity<List<DocumentDTO>> getAllDocuments() {
        logger.info("Received request to list all documents.");
        List<DocumentDTO> documents = documentService.getAllDocuments();
        return new ResponseEntity<>(documents, HttpStatus.OK);
    }

    @GetMapping("/documents/download/{id}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) {
        logger.info("Received download request for document ID: {}", id);
        DocumentDTO documentDTO = documentService.getDocumentById(id);
        Path filePath = Paths.get(documentDTO.getFilePath()).normalize();

        Resource resource;
        try {
            resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                logger.error("File not found or not readable: {}", documentDTO.getFileName());
                throw new DocumentException("File not found or not readable: " + documentDTO.getFileName());
            }
        } catch (MalformedURLException ex) {
            logger.error("Malformed URL for file: {}", documentDTO.getFileName(), ex);
            throw new DocumentException("File not found: " + documentDTO.getFileName(), ex);
        }

        // Determine content type
        String contentType = "application/octet-stream"; // Default
        try {
            contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                logger.warn("Could not determine file type for: {}. Using default.", documentDTO.getFileName());
                contentType = "application/octet-stream";
            }
        } catch (IOException ex) {
            logger.warn("Error determining file type for: {}. Using default.", documentDTO.getFileName(), ex);
        }

        logger.info("Serving file: {} with content type: {}", resource.getFilename(), contentType);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/documents/{id}")
    public ResponseEntity<?> deleteDocument(@PathVariable Long id) {
        logger.info("Received delete request for document ID: {}", id);
        try {
            documentService.deleteDocumentById(id);
            logger.info("Successfully deleted document with ID: {}", id);
            return ResponseEntity.ok().body("Document deleted successfully.");
        } catch (DocumentException ex) {
            logger.error("Error deleting document with ID: {}", id, ex);
            throw ex; // Will be handled by GlobalExceptionHandler
        }
    }
}
