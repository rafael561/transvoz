package com.transvoz.module.audio.controller;

import com.transvoz.module.audio.dto.AudioResponse;
import com.transvoz.module.audio.dto.AudioUploadResponse;
import com.transvoz.module.audio.service.AudioService;
import com.transvoz.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audio")
@RequiredArgsConstructor
@Tag(name = "Audio", description = "Audio file management endpoints")
public class AudioController {

    private final AudioService audioService;

    @PostMapping("/upload")
    @Operation(summary = "Upload audio file")
    public ResponseEntity<ApiResponse<AudioUploadResponse>> uploadAudio(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails userDetails) {
        AudioUploadResponse response = audioService.uploadAudio(file, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Audio uploaded successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get user's audio files")
    public ResponseEntity<ApiResponse<Page<AudioResponse>>> getUserAudios(
            @AuthenticationPrincipal UserDetails userDetails,
            Pageable pageable) {
        Page<AudioResponse> audios = audioService.getUserAudios(userDetails.getUsername(), pageable);
        return ResponseEntity.ok(ApiResponse.success("Audio files retrieved", audios));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get audio file details")
    public ResponseEntity<ApiResponse<AudioResponse>> getAudio(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        AudioResponse audio = audioService.getAudio(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Audio file retrieved", audio));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete audio file")
    public ResponseEntity<ApiResponse<Void>> deleteAudio(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        audioService.deleteAudio(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Audio file deleted", null));
    }
}
