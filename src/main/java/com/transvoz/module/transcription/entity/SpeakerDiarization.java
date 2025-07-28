package com.transvoz.module.transcription.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "speaker_diarizations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpeakerDiarization {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transcription_id", nullable = false)
    private Transcription transcription;

    @Column(name = "speaker_id", nullable = false)
    private String speakerId;

    @Column(name = "speaker_label")
    private String speakerLabel;

    @Column(name = "start_time", nullable = false)
    private Double startTime;

    @Column(name = "end_time", nullable = false)
    private Double endTime;

    @Column(name = "text", columnDefinition = "TEXT", nullable = false)
    private String text;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Helper methods
    public Double getDuration() {
        return endTime - startTime;
    }

    public boolean isValidTimeRange() {
        return startTime != null && endTime != null && endTime > startTime;
    }

    public String getDisplayName() {
        return speakerLabel != null ? speakerLabel : "Speaker " + speakerId;
    }

    public boolean hasHighConfidence() {
        return confidenceScore != null && confidenceScore >= 0.8;
    }

    @Override
    public String toString() {
        return String.format("SpeakerDiarization{id=%s, speaker='%s', time=%.2f-%.2f, text='%s'}",
                id, getDisplayName(), startTime, endTime,
                text != null && text.length() > 50 ? text.substring(0, 50) + "..." : text);
    }
}
