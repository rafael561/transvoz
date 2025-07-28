package com.transvoz.module.audio.dto;

import com.transvoz.shared.enums.AudioStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AudioResponse {
    private UUID id;
    private String originalFilename;
    private Long fileSize;
    private String mimeType;
    private Integer durationSeconds;
    private AudioStatus status;
    private Boolean isEncrypted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
