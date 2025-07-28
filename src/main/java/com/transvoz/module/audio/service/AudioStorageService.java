package com.transvoz.module.audio.service;

import com.transvoz.shared.exception.TransVozException;
import com.transvoz.shared.util.CryptoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AudioStorageService {

    private final S3Client s3Client;

    @Value("${app.storage.type:local}")
    private String storageType;

    @Value("${app.storage.local.upload-dir:./uploads}")
    private String localUploadDir;

    @Value("${app.storage.s3.bucket}")
    private String s3Bucket;

    public String storeFile(MultipartFile file, UUID userId) {
        String filename = generateUniqueFilename(file.getOriginalFilename());
        
        try {
            if ("s3".equalsIgnoreCase(storageType)) {
                return storeFileInS3(file, filename, userId);
            } else {
                return storeFileLocally(file, filename, userId);
            }
        } catch (Exception e) {
            log.error("Error storing file: {}", e.getMessage(), e);
            throw new TransVozException("Failed to store file");
        }
    }

    public void deleteFile(String filename, UUID userId) {
        try {
            if ("s3".equalsIgnoreCase(storageType)) {
                deleteFileFromS3(filename, userId);
            } else {
                deleteFileLocally(filename, userId);
            }
        } catch (Exception e) {
            log.error("Error deleting file: {}", e.getMessage(), e);
            throw new TransVozException("Failed to delete file");
        }
    }

    public String getFilePath(String filename, UUID userId) {
        if ("s3".equalsIgnoreCase(storageType)) {
            return String.format("s3://%s/%s/%s", s3Bucket, userId, filename);
        } else {
            return Paths.get(localUploadDir, userId.toString(), filename).toString();
        }
    }

    private String storeFileInS3(MultipartFile file, String filename, UUID userId) throws IOException {
        String key = userId + "/" + filename;
        
        // Encrypt file content
        byte[] encryptedContent = CryptoUtils.encrypt(file.getBytes());
        
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(s3Bucket)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(request, RequestBody.fromBytes(encryptedContent));
        log.info("File stored in S3: {}", key);
        
        return filename;
    }

    private String storeFileLocally(MultipartFile file, String filename, UUID userId) throws IOException {
        Path userDir = Paths.get(localUploadDir, userId.toString());
        Files.createDirectories(userDir);
        
        Path filePath = userDir.resolve(filename);
        
        // Encrypt and store file
        byte[] encryptedContent = CryptoUtils.encrypt(file.getBytes());
        Files.write(filePath, encryptedContent);
        
        log.info("File stored locally: {}", filePath);
        return filename;
    }

    private void deleteFileFromS3(String filename, UUID userId) {
        String key = userId + "/" + filename;
        
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(s3Bucket)
                .key(key)
                .build();

        s3Client.deleteObject(request);
        log.info("File deleted from S3: {}", key);
    }

    private void deleteFileLocally(String filename, UUID userId) throws IOException {
        Path filePath = Paths.get(localUploadDir, userId.toString(), filename);
        Files.deleteIfExists(filePath);
        log.info("File deleted locally: {}", filePath);
    }

    private String generateUniqueFilename(String originalFilename) {
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
        }
        return UUID.randomUUID().toString() + extension;
    }
}
