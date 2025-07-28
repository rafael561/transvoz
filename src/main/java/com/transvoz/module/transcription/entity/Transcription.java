package com.transvoz.module.transcription.entity;

import com.transvoz.module.audio.entity.Audio;
import com.transvoz.module.user.entity.User;
import com.transvoz.shared.enums.TranscriptionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "transcriptions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transcription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "audio_id", nullable = false)
    private Audio audio;

    @Column(name = "external_job_id")
    private String externalJobId;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TranscriptionStatus status = TranscriptionStatus.PENDING;

    @Column(name = "full_text", columnDefinition = "TEXT")
    private String fullText;

    @Column(name = "language_detected")
    private String languageDetected;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Column(name = "processing_time_seconds")
    private Integer processingTimeSeconds;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "webhook_received_at")
    private LocalDateTime webhookReceivedAt;

    @OneToMany(mappedBy = "transcription", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<SpeakerDiarization> speakerDiarizations = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Helper methods
    public void addSpeakerDiarization(SpeakerDiarization diarization) {
        speakerDiarizations.add(diarization);
        diarization.setTranscription(this);
    }

    public void removeSpeakerDiarization(SpeakerDiarization diarization) {
        speakerDiarizations.remove(diarization);
        diarization.setTranscription(null);
    }

    public void clearSpeakerDiarizations() {
        speakerDiarizations.forEach(diarization -> diarization.setTranscription(null));
        speakerDiarizations.clear();
    }

    public boolean isCompleted() {
        return TranscriptionStatus.COMPLETED.equals(this.status);
    }

    public boolean isFailed() {
        return TranscriptionStatus.FAILED.equals(this.status);
    }

    public boolean isProcessing() {
        return TranscriptionStatus.PROCESSING.equals(this.status);
    }

    public boolean isPending() {
        return TranscriptionStatus.PENDING.equals(this.status);
    }
}
