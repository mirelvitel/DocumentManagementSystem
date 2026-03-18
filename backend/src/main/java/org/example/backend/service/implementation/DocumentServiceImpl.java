package org.example.backend.service.implementation;

import lombok.RequiredArgsConstructor;
import org.example.backend.config.FileStorageProperties;
import org.example.backend.config.RabbitMQConfig;
import org.example.backend.exception.DocumentException;
import org.example.backend.persistence.elasticsearch.DocumentSearchEntity;
import org.example.backend.persistence.elasticsearch.DocumentSearchRepository;
import org.example.backend.persistence.entity.DocumentEntity;
import org.example.backend.persistence.repository.DocumentRepository;
import org.example.backend.service.DocumentService;
import org.example.backend.service.dto.DocumentDTO;
import org.example.backend.service.dto.DocumentMessage;
import org.example.backend.service.mapper.DocumentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final FileStorageProperties fileStorageProperties;
    private final DocumentMapper documentMapper;
    private final RabbitTemplate rabbitTemplate;
    private final DocumentSearchRepository documentSearchRepository;

    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);
    private Path fileStorageLocation;

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

    @Override
    public DocumentDTO uploadDocument(MultipartFile file) {
        String fileName = storeFile(file);
        String filePath = this.fileStorageLocation.resolve(fileName).toString();
        String title = extractTitleFromFileName(file.getOriginalFilename());

        DocumentEntity documentEntity = new DocumentEntity(title, fileName, filePath);
        DocumentEntity savedDocumentEntity = documentRepository.save(documentEntity);

        DocumentMessage message = new DocumentMessage(
                savedDocumentEntity.getId(),
                savedDocumentEntity.getTitle(),
                savedDocumentEntity.getFileName(),
                savedDocumentEntity.getFilePath()
        );

        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.DOCUMENT_UPLOAD_QUEUE, message);
            logger.info("Sent message to RabbitMQ for document ID: {}", savedDocumentEntity.getId());
        } catch (Exception ex) {
            logger.error("Failed to send message to RabbitMQ for document ID: {}", savedDocumentEntity.getId(), ex);
            throw new DocumentException("Failed to queue document for OCR processing.", ex);
        }

        return documentMapper.toDTO(savedDocumentEntity);
    }

    @Override
    public List<DocumentDTO> getAllDocuments() {
        return documentRepository.findAll().stream()
                .map(documentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DocumentDTO getDocumentById(Long id) {
        DocumentEntity documentEntity = documentRepository.findById(id)
                .orElseThrow(() -> new DocumentException("Document not found with id " + id));
        return documentMapper.toDTO(documentEntity);
    }

    @Override
    public void deleteDocumentById(Long id) {
        DocumentEntity documentEntity = documentRepository.findById(id)
                .orElseThrow(() -> new DocumentException("Document not found with id " + id));

        // Delete the file from disk
        Path filePath = Paths.get(documentEntity.getFilePath()).normalize();
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            logger.error("Could not delete file: {}", documentEntity.getFileName(), ex);
        }

        // Remove from Elasticsearch index
        try {
            documentSearchRepository.deleteDocument(String.valueOf(id));
            logger.info("Removed document ID {} from Elasticsearch", id);
        } catch (IOException ex) {
            logger.error("Failed to remove document ID {} from Elasticsearch", id, ex);
        }

        documentRepository.delete(documentEntity);
    }

    @Override
    public void updateDocumentText(Long documentId, String extractedText) {
        DocumentEntity documentEntity = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentException("Document not found with id " + documentId));

        documentEntity.setTextContent(extractedText);
        documentRepository.save(documentEntity);

        try {
            DocumentSearchEntity searchEntity = documentMapper.toSearchEntity(documentEntity);
            documentSearchRepository.indexDocument(searchEntity);
            logger.info("Indexed document with ID: {}", documentEntity.getId());
        } catch (IOException e) {
            logger.error("Failed to index document with ID: {}", documentEntity.getId(), e);
            throw new DocumentException("Failed to index document with ID: " + documentEntity.getId(), e);
        }
    }

    @Override
    public List<DocumentDTO> searchDocuments(String keyword) {
        try {
            List<DocumentSearchEntity> searchResults = documentSearchRepository.searchByContent(keyword);
            return searchResults.stream()
                    .map(documentMapper::searchEntityToDTO)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Error while searching documents in Elasticsearch", e);
            throw new DocumentException("Error while searching documents", e);
        }
    }

    /**
     * Returns the file path for a document (used internally for downloads).
     */
    public Path getDocumentFilePath(Long id) {
        DocumentEntity documentEntity = documentRepository.findById(id)
                .orElseThrow(() -> new DocumentException("Document not found with id " + id));
        return Paths.get(documentEntity.getFilePath()).normalize();
    }

    private String storeFile(MultipartFile file) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (originalFileName.contains("..")) {
                throw new DocumentException("Invalid path sequence in file name " + originalFileName);
            }

            // Prefix with UUID to avoid name collisions
            String storedFileName = UUID.randomUUID() + "_" + originalFileName;
            Path targetLocation = this.fileStorageLocation.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return storedFileName;
        } catch (IOException ex) {
            throw new DocumentException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }

    private String extractTitleFromFileName(String fileName) {
        if (fileName == null) return "Untitled";
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
    }
}