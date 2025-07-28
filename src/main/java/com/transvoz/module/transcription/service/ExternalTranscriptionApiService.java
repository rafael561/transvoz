package com.transvoz.module.transcription.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transvoz.module.audio.entity.Audio;
import com.transvoz.module.transcription.dto.ExternalApiResponse;
import com.transvoz.module.transcription.dto.TranscriptionRequest;
import com.transvoz.shared.exception.TransVozException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalTranscriptionApiService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.transcription.external-api.url}")
    private String apiUrl;

    @Value("${app.transcription.external-api.api-key}")
    private String apiKey;

    @Value("${app.transcription.external-api.webhook-url}")
    private String webhookUrl;

    @Value("${app.transcription.external-api.timeout:30000}")
    private int timeout;

    public String submitTranscriptionJob(Audio audio, TranscriptionRequest request) {
        try {
            log.info("Submitting transcription job for audio: {}", audio.getId());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("audio_url", audio.getFilePath());
            requestBody.put("speaker_diarization", request.getEnableDiarization());
            requestBody.put("max_speakers", request.getMaxSpeakers());
            requestBody.put("webhook_url", webhookUrl);
            requestBody.put("language", request.getLanguage());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    apiUrl + "/transcribe", entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                String jobId = (String) responseBody.get("job_id");
                log.info("Transcription job submitted successfully: {}", jobId);
                return jobId;
            } else {
                throw new TransVozException("Failed to submit transcription job");
            }

        } catch (Exception e) {
            log.error("Error submitting transcription job: {}", e.getMessage(), e);
            throw new TransVozException("Failed to submit transcription job: " + e.getMessage());
        }
    }

    public ExternalApiResponse getTranscriptionStatus(String jobId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(apiKey);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<ExternalApiResponse> response = restTemplate.exchange(
                    apiUrl + "/status/" + jobId, HttpMethod.GET, entity, ExternalApiResponse.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new TransVozException("Failed to get transcription status");
            }

        } catch (Exception e) {
            log.error("Error getting transcription status: {}", e.getMessage(), e);
            throw new TransVozException("Failed to get transcription status: " + e.getMessage());
        }
    }
}
