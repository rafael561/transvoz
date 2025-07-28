package com.transvoz.module.webhook.service;

import com.transvoz.module.transcription.entity.SpeakerDiarization;
import com.transvoz.module.transcription.entity.Transcription;
import com.transvoz.module.transcription.repository.SpeakerDiarizationRepository;
import com.transvoz.module.transcription.repository.TranscriptionRepository;
import com.transvoz.module.webhook.dto.WebhookPayload;
import com.transvoz.shared.enums.TranscriptionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WebhookService {

    private final TranscriptionRepository transcriptionRepository;
    private final SpeakerDiarizationRepository speakerDiarizationRepository;

    public void processTranscriptionWebhook(WebhookPayload payload) {
        log.info("Processing webhook for job: {}", payload.getJobId());

        Transcription transcription = transcriptionRepository.findByExternalJobId(payload.getJobId())
                .orElseThrow(() -> new RuntimeException("Transcription not found for job: " + payload.getJobId()));

        // Update transcription status
        transcription.setStatus(mapStatus(payload.getStatus()));
        transcription.setFullText(payload.getText());
        transcription.setLanguageDetected(payload.getLanguage());
        transcription.setConfidenceScore(payload.getConfidence());
        transcription.setProcessingTimeSeconds(payload.getProcessingTime());
        transcription.setErrorMessage(payload.getErrorMessage());
        transcription.setWebhookReceivedAt(LocalDateTime.now());

        transcriptionRepository.save(transcription);

        // Process speaker diarization if available
        if (payload.getSpeakers() != null && !payload.getSpeakers().isEmpty()) {
            processSpeakerDiarization(transcription, payload.getSpeakers());
        }

        log.info("Webhook processed successfully for job: {}", payload.getJobId());
    }

    private void processSpeakerDiarization(Transcription transcription, List<WebhookPayload.WebhookSpeaker> speakers) {
        // Delete existing diarizations
        speakerDiarizationRepository.deleteByTranscriptionId(transcription.getId());

        // Create new diarizations
        List<SpeakerDiarization> diarizations = speakers.stream()
                .map(speaker -> SpeakerDiarization.builder()
                        .transcription(transcription)
                        .speakerId(speaker.getSpeaker())
                        .speakerLabel(generateSpeakerLabel(speaker.getSpeaker()))
                        .startTime(speaker.getStartTime())
                        .endTime(speaker.getEndTime())
                        .text(speaker.getText())
                        .confidenceScore(speaker.getConfidence())
                        .build())
                .collect(Collectors.toList());

        speakerDiarizationRepository.saveAll(diarizations);
        log.info("Saved {} speaker diarizations for transcription: {}", 
                diarizations.size(), transcription.getId());
    }

    private String generateSpeakerLabel(String speakerId) {
        // Generate human-friendly speaker labels
        try {
            int speakerNumber = Integer.parseInt(speakerId.replaceAll("\\D", ""));
            return "Speaker " + (speakerNumber + 1);
        } catch (NumberFormatException e) {
            return "Speaker " + speakerId;
        }
    }

    private TranscriptionStatus mapStatus(String externalStatus) {
        return switch (externalStatus.toLowerCase()) {
            case "completed", "success" -> TranscriptionStatus.COMPLETED;
            case "processing", "in_progress" -> TranscriptionStatus.PROCESSING;
            case "failed", "error" -> TranscriptionStatus.FAILED;
            case "cancelled" -> TranscriptionStatus.CANCELLED;
            default -> TranscriptionStatus.PENDING;
        };
    }
}
