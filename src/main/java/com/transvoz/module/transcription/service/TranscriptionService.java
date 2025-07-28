package com.transvoz.module.transcription.service;

import com.transvoz.module.audio.entity.Audio;
import com.transvoz.module.audio.repository.AudioRepository;
import com.transvoz.module.transcription.dto.SpeakerSegment;
import com.transvoz.module.transcription.dto.TranscriptionRequest;
import com.transvoz.module.transcription.dto.TranscriptionResponse;
import com.transvoz.module.transcription.entity.SpeakerDiarization;
import com.transvoz.module.transcription.entity.Transcription;
import com.transvoz.module.transcription.repository.SpeakerDiarizationRepository;
import com.transvoz.module.transcription.repository.TranscriptionRepository;
import com.transvoz.module.user.entity.User;
import com.transvoz.module.user.service.UserService;
import com.transvoz.shared.exception.ResourceNotFoundException;
import com.transvoz.shared.exception.TransVozException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TranscriptionService {

    private final TranscriptionRepository transcriptionRepository;
    private final SpeakerDiarizationRepository speakerDiarizationRepository;
    private final AudioRepository audioRepository;
    private final UserService userService;
    private final ExternalTranscriptionApiService externalApiService;

    public TranscriptionResponse startTranscription(TranscriptionRequest request, String userEmail) {
        log.info("Starting transcription for audio: {}", request.getAudioId());

        User user = userService.findByEmail(userEmail);
        Audio audio = audioRepository.findByIdAndUserId(request.getAudioId(), user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Audio not found"));

        // Check if transcription already exists
        if (transcriptionRepository.findByUserIdAndStatus(user.getId(), 
                com.transvoz.shared.enums.TranscriptionStatus.PENDING).size() > 10) {
            throw new TransVozException("Too many pending transcriptions. Please wait.");
        }

        Transcription transcription = Transcription.builder()
                .user(user)
                .audio(audio)
                .build();

        Transcription savedTranscription = transcriptionRepository.save(transcription);
        
        // Submit to external API
        String externalJobId = externalApiService.submitTranscriptionJob(audio, request);
        savedTranscription.setExternalJobId(externalJobId);
        transcriptionRepository.save(savedTranscription);

        log.info("Transcription started with ID: {} and external job ID: {}", 
                savedTranscription.getId(), externalJobId);

        return mapToTranscriptionResponse(savedTranscription);
    }

    @Transactional(readOnly = true)
    public Page<TranscriptionResponse> getUserTranscriptions(String userEmail, Pageable pageable) {
        User user = userService.findByEmail(userEmail);
        Page<Transcription> transcriptions = transcriptionRepository
                .findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);
        return transcriptions.map(this::mapToTranscriptionResponse);
    }

    @Transactional(readOnly = true)
    public TranscriptionResponse getTranscription(UUID transcriptionId, String userEmail) {
        User user = userService.findByEmail(userEmail);
        Transcription transcription = transcriptionRepository.findByIdAndUserId(transcriptionId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Transcription not found"));
        return mapToTranscriptionResponse(transcription);
    }

    public void deleteTranscription(UUID transcriptionId, String userEmail) {
        User user = userService.findByEmail(userEmail);
        Transcription transcription = transcriptionRepository.findByIdAndUserId(transcriptionId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Transcription not found"));

        // Delete speaker diarizations first
        speakerDiarizationRepository.deleteByTranscriptionId(transcriptionId);
        
        // Delete transcription
        transcriptionRepository.delete(transcription);
        log.info("Transcription deleted: {}", transcriptionId);
    }

    private TranscriptionResponse mapToTranscriptionResponse(Transcription transcription) {
        List<SpeakerSegment> speakers = speakerDiarizationRepository
                .findByTranscriptionIdOrderByStartTime(transcription.getId())
                .stream()
                .map(this::mapToSpeakerSegment)
                .collect(Collectors.toList());

        return TranscriptionResponse.builder()
                .id(transcription.getId())
                .audioId(transcription.getAudio().getId())
                .externalJobId(transcription.getExternalJobId())
                .status(transcription.getStatus())
                .fullText(transcription.getFullText())
                .languageDetected(transcription.getLanguageDetected())
                .confidenceScore(transcription.getConfidenceScore())
                .processingTimeSeconds(transcription.getProcessingTimeSeconds())
                .errorMessage(transcription.getErrorMessage())
                .speakers(speakers)
                .createdAt(transcription.getCreatedAt())
                .updatedAt(transcription.getUpdatedAt())
                .webhookReceivedAt(transcription.getWebhookReceivedAt())
                .build();
    }

    private SpeakerSegment mapToSpeakerSegment(SpeakerDiarization diarization) {
        return SpeakerSegment.builder()
                .speakerId(diarization.getSpeakerId())
                .speakerLabel(diarization.getSpeakerLabel())
                .startTime(diarization.getStartTime())
                .endTime(diarization.getEndTime())
                .text(diarization.getText())
                .confidenceScore(diarization.getConfidenceScore())
                .build();
    }
}
