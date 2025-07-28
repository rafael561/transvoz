package com.transvoz.module.transcription.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SpeakerSegment {
    private String speakerId;
    private String speakerLabel;
    private Double startTime;
    private Double endTime;
    private String text;
    private Double confidenceScore;
}
