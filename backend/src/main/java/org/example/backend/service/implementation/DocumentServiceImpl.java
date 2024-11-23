package org.example.backend.service.implementation;

import lombok.RequiredArgsConstructor;
import org.example.backend.config.FileStorageProperties;
import org.example.backend.config.RabbitMQConfig;
import org.example.backend.persistence.entity.DocumentEntity;
import org.example.backend.service.DocumentService;
import org.example.backend.service.dto.DocumentDTO;
import org.example.backend.exception.DocumentException;
import org.example.backend.service.dto.DocumentMessage;
import org.example.backend.service.mapper.DocumentMapper;
import org.example.backend.persistence.repository.DocumentRepository;
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
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final FileStorageProperties fileStorageProperties;
    private final DocumentMapper documentMapper;
    private final RabbitTemplate rabbitTemplate;

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
        String title = extractTitleFromFileName(fileName);

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
            logger.info("Sent message to RabbitMQ: {}", message);
        } catch (Exception ex) {
            logger.error("Failed to send message to RabbitMQ for document ID: {}", savedDocumentEntity.getId(), ex);
            throw new DocumentException("Failed to send message to RabbitMQ for document ID: " + savedDocumentEntity.getId(), ex);
        }

        return documentMapper.toDTO(savedDocumentEntity);
    }

    public List<DocumentDTO> getAllDocuments() {
        List<DocumentEntity> documentEntities = documentRepository.findAll();
        return documentEntities.stream()
                .map(documentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DocumentDTO getDocumentById(Long id) {
        DocumentEntity documentEntity = documentRepository.findById(id)
                .orElseThrow(() -> new DocumentException("Document not found with id " + id));
        return documentMapper.toDTO(documentEntity);
    }


    public void deleteDocumentById(Long id) {
        DocumentEntity documentEntity = documentRepository.findById(id)
                .orElseThrow(() -> new DocumentException("Document not found with id " + id));

        Path filePath = Paths.get(documentEntity.getFilePath()).normalize();

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new DocumentException("Could not delete file: " + documentEntity.getFileName(), ex);
        }

        documentRepository.delete(documentEntity);
    }

    private String storeFile(MultipartFile file) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (originalFileName.contains("..")) {
                throw new DocumentException("Invalid path sequence in file name " + originalFileName);
            }

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

    @Override
    public void updateDocumentText(Long documentId, String extractedText) {
        DocumentEntity documentEntity = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentException("Document not found with id " + documentId));

        documentEntity.setTextContent(extractedText);
        documentRepository.save(documentEntity);
    }

}
