package com.transvoz.module.transcription.dto;

import com.transvoz.shared.enums.TranscriptionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class TranscriptionResponse {
    private UUID id;
    private UUID audioId;
    private String externalJobId;
    private TranscriptionStatus status;
    private String fullText;
    private String languageDetected;
    private Double confidenceScore;
    private Integer processingTimeSeconds;
    private String errorMessage;
    private List<SpeakerSegment> speakers;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime webhookReceivedAt;
}
