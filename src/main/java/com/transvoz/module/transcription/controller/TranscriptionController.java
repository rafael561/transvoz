package com.transvoz.module.transcription.controller;

import com.transvoz.module.transcription.dto.TranscriptionRequest;
import com.transvoz.module.transcription.dto.TranscriptionResponse;
import com.transvoz.module.transcription.service.TranscriptionService;
import com.transvoz.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transcriptions")
@RequiredArgsConstructor
@Tag(name = "Transcription", description = "Audio transcription endpoints")
public class TranscriptionController {

    private final TranscriptionService transcriptionService;

    @PostMapping
    @Operation(summary = "Start transcription")
    public ResponseEntity<ApiResponse<TranscriptionResponse>> startTranscription(
            @Valid @RequestBody TranscriptionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        TranscriptionResponse response = transcriptionService.startTranscription(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Transcription started", response));
    }

    @GetMapping
    @Operation(summary = "Get user's transcriptions")
    public ResponseEntity<ApiResponse<Page<TranscriptionResponse>>> getUserTranscriptions(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable) {
        Page<TranscriptionResponse> transcriptions = transcriptionService.getUserTranscriptions(
                userDetails.getUsername(), pageable);
        return ResponseEntity.ok(ApiResponse.success("Transcriptions retrieved", transcriptions));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transcription details")
    public ResponseEntity<ApiResponse<TranscriptionResponse>> getTranscription(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        TranscriptionResponse transcription = transcriptionService.getTranscription(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Transcription retrieved", transcription));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete transcription")
    public ResponseEntity<ApiResponse<Void>> deleteTranscription(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        transcriptionService.deleteTranscription(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Transcription deleted", null));
    }
}
