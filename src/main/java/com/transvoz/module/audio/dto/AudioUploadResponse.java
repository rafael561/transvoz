package com.transvoz.module.audio.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AudioUploadResponse {
    private UUID id;
    private String originalFilename;
    private Long fileSize;
    private String mimeType;
    private Integer durationSeconds;
    private String status;
}
