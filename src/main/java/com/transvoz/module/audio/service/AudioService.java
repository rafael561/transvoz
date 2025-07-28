package com.transvoz.module.audio.service;

import com.transvoz.module.audio.dto.AudioResponse;
import com.transvoz.module.audio.dto.AudioUploadResponse;
import com.transvoz.module.audio.entity.Audio;
import com.transvoz.module.audio.repository.AudioRepository;
import com.transvoz.module.user.entity.User;
import com.transvoz.module.user.service.UserService;
import com.transvoz.shared.exception.InvalidFileException;
import com.transvoz.shared.exception.ResourceNotFoundException;
import com.transvoz.shared.util.AudioUtils;
import com.transvoz.shared.util.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AudioService {

    private final AudioRepository audioRepository;
    private final UserService userService;
    private final AudioStorageService audioStorageService;

    @Value("${app.audio.max-duration-hours:4}")
    private int maxDurationHours;

    @Value("${app.audio.max-file-size-mb:200}")
    private int maxFileSizeMb;

    @Value("${app.audio.allowed-formats}")
    private String allowedFormats;

    public AudioUploadResponse uploadAudio(MultipartFile file, String userEmail) {
        log.info("Starting audio upload for user: {}", userEmail);

        validateAudioFile(file);

        User user = userService.findByEmail(userEmail);
        
        // Store file
        String storedFilename = audioStorageService.storeFile(file, user.getId());
        
        // Extract audio metadata
        Integer durationSeconds = AudioUtils.extractDuration(file);
        String checksum = FileUtils.calculateChecksum(file);

        Audio audio = Audio.builder()
                .user(user)
                .originalFilename(file.getOriginalFilename())
                .storedFilename(storedFilename)
                .filePath(audioStorageService.getFilePath(storedFilename, user.getId()))
                .fileSize(file.getSize())
                .mimeType(file.getContentType())
                .durationSeconds(durationSeconds)
                .checksum(checksum)
                .build();

        Audio savedAudio = audioRepository.save(audio);
        log.info("Audio uploaded successfully with ID: {}", savedAudio.getId());

        return AudioUploadResponse.builder()
                .id(savedAudio.getId())
                .originalFilename(savedAudio.getOriginalFilename())
                .fileSize(savedAudio.getFileSize())
                .mimeType(savedAudio.getMimeType())
                .durationSeconds(savedAudio.getDurationSeconds())
                .status(savedAudio.getStatus().name())
                .build();
    }

    @Transactional(readOnly = true)
    public Page<AudioResponse> getUserAudios(String userEmail, Pageable pageable) {
        User user = userService.findByEmail(userEmail);
        Page<Audio> audios = audioRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);
        return audios.map(this::mapToAudioResponse);
    }

    @Transactional(readOnly = true)
    public AudioResponse getAudio(UUID audioId, String userEmail) {
        User user = userService.findByEmail(userEmail);
        Audio audio = audioRepository.findByIdAndUserId(audioId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Audio not found"));
        return mapToAudioResponse(audio);
    }

    public void deleteAudio(UUID audioId, String userEmail) {
        User user = userService.findByEmail(userEmail);
        Audio audio = audioRepository.findByIdAndUserId(audioId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Audio not found"));

        // Delete physical file
        audioStorageService.deleteFile(audio.getStoredFilename(), user.getId());
        
        // Delete database record
        audioRepository.delete(audio);
        log.info("Audio deleted successfully: {}", audioId);
    }

    private void validateAudioFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidFileException("File is empty");
        }

        // Check file size
        long maxSizeBytes = (long) maxFileSizeMb * 1024 * 1024;
        if (file.getSize() > maxSizeBytes) {
            throw new InvalidFileException("File size exceeds maximum allowed size of " + maxFileSizeMb + "MB");
        }

        // Check file format
        String extension = FileUtils.getFileExtension(file.getOriginalFilename());
        List<String> allowedExtensions = Arrays.asList(allowedFormats.split(","));
        if (!allowedExtensions.contains(extension.toLowerCase())) {
            throw new InvalidFileException("File format not supported. Allowed formats: " + allowedFormats);
        }

        // Validate MIME type
        if (!AudioUtils.isValidAudioMimeType(file.getContentType())) {
            throw new InvalidFileException("Invalid audio file");
        }
    }

    private AudioResponse mapToAudioResponse(Audio audio) {
        return AudioResponse.builder()
                .id(audio.getId())
                .originalFilename(audio.getOriginalFilename())
                .fileSize(audio.getFileSize())
                .mimeType(audio.getMimeType())
                .durationSeconds(audio.getDurationSeconds())
                .status(audio.getStatus())
                .isEncrypted(audio.getIsEncrypted())
                .createdAt(audio.getCreatedAt())
                .updatedAt(audio.getUpdatedAt())
                .build();
    }
}
