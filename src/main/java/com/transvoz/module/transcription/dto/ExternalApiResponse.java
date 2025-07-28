package com.transvoz.module.transcription.dto;

import lombok.Data;

import java.util.List;

@Data
public class ExternalApiResponse {
    private String jobId;
    private String status;
    private String text;
    private String language;
    private Double confidence;
    private Integer processingTime;
    private List<ExternalSpeakerSegment> speakers;
    private String errorMessage;

    @Data
    public static class ExternalSpeakerSegment {
        private String speaker;
        private Double startTime;
        private Double endTime;
        private String text;
        private Double confidence;
    }
}
