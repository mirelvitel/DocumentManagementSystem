package org.example.backend.service.implementation;

import lombok.RequiredArgsConstructor;
import org.example.backend.config.RabbitMQConfig;
import org.example.backend.exception.DocumentException;
import org.example.backend.persistence.elasticsearch.DocumentSearchEntity;
import org.example.backend.persistence.elasticsearch.DocumentSearchRepository;
import org.example.backend.persistence.entity.DocumentEntity;
import org.example.backend.persistence.repository.DocumentRepository;
import org.example.backend.service.DocumentService;
import org.example.backend.service.MinIOService;
import org.example.backend.service.dto.DocumentDTO;
import org.example.backend.service.dto.DocumentMessage;
import org.example.backend.service.mapper.DocumentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;
    private final RabbitTemplate rabbitTemplate;
    private final DocumentSearchRepository documentSearchRepository;
    private final MinIOService minIOService;

    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);

    @Override
    public DocumentDTO uploadDocument(MultipartFile file) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        if (originalFileName.contains("..")) {
            throw new DocumentException("Invalid path sequence in file name " + originalFileName);
        }

        String objectName = minIOService.uploadFile(file);
        String title = extractTitleFromFileName(file.getOriginalFilename());

        DocumentEntity documentEntity = new DocumentEntity(title, originalFileName, objectName);
        documentEntity.setOcrStatus(DocumentEntity.OcrStatus.PENDING);
        DocumentEntity savedDocumentEntity = documentRepository.save(documentEntity);

        DocumentMessage message = new DocumentMessage(
                savedDocumentEntity.getId(),
                savedDocumentEntity.getTitle(),
                savedDocumentEntity.getFileName(),
                savedDocumentEntity.getFilePath()
        );

        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.DOCUMENT_UPLOAD_QUEUE, message);
            savedDocumentEntity.setOcrStatus(DocumentEntity.OcrStatus.PROCESSING);
            documentRepository.save(savedDocumentEntity);
            logger.info("Sent message to RabbitMQ for document ID: {}", savedDocumentEntity.getId());
        } catch (Exception ex) {
            savedDocumentEntity.setOcrStatus(DocumentEntity.OcrStatus.FAILED);
            documentRepository.save(savedDocumentEntity);
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

        minIOService.deleteFile(documentEntity.getFilePath());

        try {
            documentSearchRepository.deleteDocument(String.valueOf(id));
            logger.info("Removed document ID {} from Elasticsearch", id);
        } catch (Exception ex) {
            logger.warn("Could not remove document ID {} from Elasticsearch (may not be indexed): {}", id, ex.getMessage());
        }

        documentRepository.delete(documentEntity);
    }

    @Override
    public void updateDocumentText(Long documentId, String extractedText) {
        DocumentEntity documentEntity = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentException("Document not found with id " + documentId));

        documentEntity.setTextContent(extractedText);
        documentEntity.setOcrStatus(DocumentEntity.OcrStatus.COMPLETED);
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
                    .map(searchEntity -> {
                        DocumentDTO dto = documentMapper.searchEntityToDTO(searchEntity);
                        documentRepository.findById(searchEntity.getId()).ifPresent(entity -> {
                                dto.setOcrStatus(entity.getOcrStatus() != null ? entity.getOcrStatus().name() : null);
                                dto.setCreatedAt(entity.getCreatedAt());
                        });
                        return dto;
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Error while searching documents in Elasticsearch", e);
            throw new DocumentException("Error while searching documents", e);
        }
    }

    public InputStream getDocumentFile(Long id) {
        DocumentEntity documentEntity = documentRepository.findById(id)
                .orElseThrow(() -> new DocumentException("Document not found with id " + id));
        return minIOService.downloadFile(documentEntity.getFilePath());
    }

    private String extractTitleFromFileName(String fileName) {
        if (fileName == null) return "Untitled";
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
    }
}