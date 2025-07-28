package com.transvoz.module.webhook.controller;

import com.transvoz.module.webhook.dto.WebhookPayload;
import com.transvoz.module.webhook.service.WebhookService;
import com.transvoz.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/webhook")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Webhook", description = "External API webhook endpoints")
public class WebhookController {

    private final WebhookService webhookService;

    @PostMapping("/transcription")
    @Operation(summary = "Receive transcription webhook")
    public ResponseEntity<ApiResponse<Void>> receiveTranscriptionWebhook(@RequestBody WebhookPayload payload) {
        log.info("Received transcription webhook for job: {}", payload.getJobId());
        webhookService.processTranscriptionWebhook(payload);
        return ResponseEntity.ok(ApiResponse.success("Webhook processed successfully", null));
    }
}
