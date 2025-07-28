package com.transvoz.module.transcription;

import com.transvoz.module.audio.entity.Audio;
import com.transvoz.module.audio.repository.AudioRepository;
import com.transvoz.module.transcription.dto.TranscriptionRequest;
import com.transvoz.module.transcription.entity.Transcription;
import com.transvoz.module.transcription.repository.TranscriptionRepository;
import com.transvoz.module.transcription.service.ExternalTranscriptionApiService;
import com.transvoz.module.transcription.service.TranscriptionService;
import com.transvoz.module.user.entity.User;
import com.transvoz.module.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TranscriptionServiceTest {

    @Mock
    private TranscriptionRepository transcriptionRepository;

    @Mock
    private AudioRepository audioRepository;

    @Mock
    private UserService userService;

    @Mock
    private ExternalTranscriptionApiService externalApiService;

    @InjectMocks
    private TranscriptionService transcriptionService;

    private User user;
    private Audio audio;
    private TranscriptionRequest request;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .build();

        audio = Audio.builder()
                .id(UUID.randomUUID())
                .user(user)
                .originalFilename("test.mp3")
                .build();

        request = new TranscriptionRequest();
        request.setAudioId(audio.getId());
        request.setEnableDiarization(true);
        request.setMaxSpeakers(6);
    }

    @Test
    void startTranscription_ShouldReturnResponse_WhenValidRequest() {
        // Given
        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        when(audioRepository.findByIdAndUserId(audio.getId(), user.getId())).thenReturn(Optional.of(audio));
        when(transcriptionRepository.save(any(Transcription.class))).thenReturn(
                Transcription.builder().id(UUID.randomUUID()).build());
        when(externalApiService.submitTranscriptionJob(any(), any())).thenReturn("job-123");

        // When
        var result = transcriptionService.startTranscription(request, user.getEmail());

        // Then
        assertNotNull(result);
        verify(transcriptionRepository, times(2)).save(any(Transcription.class));
        verify(externalApiService, times(1)).submitTranscriptionJob(any(), any());
    }
}
