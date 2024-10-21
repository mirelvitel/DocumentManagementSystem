package org.example.backend.service;

import org.example.backend.config.FileStorageProperties;
import org.example.backend.persistence.entity.DocumentEntity;
import org.example.backend.service.dto.DocumentDTO;
import org.example.backend.exception.DocumentException;
import org.example.backend.service.mapper.DocumentMapper;
import org.example.backend.persistence.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final FileStorageProperties fileStorageProperties;
    private final DocumentMapper documentMapper;

    private Path fileStorageLocation;

    @Autowired
    public DocumentService(DocumentRepository documentRepository,
                           FileStorageProperties fileStorageProperties,
                           DocumentMapper documentMapper) {
        this.documentRepository = documentRepository;
        this.fileStorageProperties = fileStorageProperties;
        this.documentMapper = documentMapper;
    }

    @PostConstruct
    public void init() {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new DocumentException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public DocumentDTO uploadDocument(MultipartFile file) {
        String fileName = storeFile(file);
        String filePath = this.fileStorageLocation.resolve(fileName).toString();

        String title = extractTitleFromFileName(fileName);
        DocumentEntity documentEntity = new DocumentEntity(title, fileName, filePath);

        DocumentEntity savedDocumentEntity = documentRepository.save(documentEntity);

        return documentMapper.toDTO(savedDocumentEntity);
    }

    public List<DocumentDTO> getAllDocuments() {
        List<DocumentEntity> documentEntities = documentRepository.findAll();
        return documentEntities.stream()
                .map(documentMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<DocumentDTO> searchDocumentsByTitle(String title) {
        List<DocumentEntity> documentEntities = documentRepository.findByTitleContainingIgnoreCase(title);
        return documentEntities.stream()
                .map(documentMapper::toDTO)
                .collect(Collectors.toList());
    }

    private String storeFile(MultipartFile file) {
        // Normalize file name
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check for invalid characters
            if (originalFileName.contains("..")) {
                throw new DocumentException("Invalid path sequence in file name " + originalFileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(originalFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return originalFileName;
        } catch (IOException ex) {
            throw new DocumentException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }

    private String extractTitleFromFileName(String fileName) {
        if (fileName == null) return "Untitled";
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
    }

    public DocumentDTO getDocumentById(Long id) {
        DocumentEntity documentEntity = documentRepository.findById(id)
                .orElseThrow(() -> new DocumentException("Document not found with id " + id));
        return new DocumentDTO(documentEntity.getId(), documentEntity.getTitle(),
                documentEntity.getFileName(), documentEntity.getFilePath());
    }

    public void deleteDocumentById(Long id) {
        DocumentEntity documentEntity = documentRepository.findById(id)
                .orElseThrow(() -> new DocumentException("Document not found with id " + id));

        Path filePath = Paths.get(documentEntity.getFilePath()).normalize();

        try {
            // Delete the file from the filesystem
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new DocumentException("Could not delete file: " + documentEntity.getFileName(), ex);
        }

        // Delete the database record
        documentRepository.delete(documentEntity);
    }
}
