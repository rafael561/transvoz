package com.transvoz.module.transcription.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class TranscriptionRequest {
    @NotNull(message = "Audio ID is required")
    private UUID audioId;
    
    private String language; // Optional: specify language or auto-detect
    private Boolean enableDiarization = true;
    private Integer maxSpeakers = 6;
}
