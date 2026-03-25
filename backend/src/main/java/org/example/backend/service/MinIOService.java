package org.example.backend.service;

import io.minio.*;
import org.example.backend.exception.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
public class MinIOService {

    private static final Logger logger = LoggerFactory.getLogger(MinIOService.class);

    private final MinioClient minioClient;
    private final String bucket;

    public MinIOService(MinioClient minioClient, @Value("${minio.bucket}") String bucket) {
        this.minioClient = minioClient;
        this.bucket = bucket;
    }

    public String uploadFile(MultipartFile file) {
        String objectName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            logger.info("Uploaded file to MinIO: {}", objectName);
            return objectName;
        } catch (Exception e) {
            throw new DocumentException("Failed to upload file to MinIO", e);
        }
    }

    public InputStream downloadFile(String objectName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            throw new DocumentException("Failed to download file from MinIO: " + objectName, e);
        }
    }

    public void deleteFile(String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .build()
            );
            logger.info("Deleted file from MinIO: {}", objectName);
        } catch (Exception e) {
            logger.error("Failed to delete file from MinIO: {}", objectName, e);
        }
    }
}