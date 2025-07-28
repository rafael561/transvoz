package com.transvoz.module.audio;

import com.transvoz.module.audio.entity.Audio;
import com.transvoz.module.audio.repository.AudioRepository;
import com.transvoz.module.audio.service.AudioService;
import com.transvoz.module.audio.service.AudioStorageService;
import com.transvoz.module.user.entity.User;
import com.transvoz.module.user.service.UserService;
import com.transvoz.shared.exception.InvalidFileException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AudioServiceTest {

    @Mock
    private AudioRepository audioRepository;

    @Mock
    private UserService userService;

    @Mock
    private AudioStorageService audioStorageService;

    @InjectMocks
    private AudioService audioService;

    private User user;
    private MockMultipartFile validAudioFile;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .build();

        validAudioFile = new MockMultipartFile(
                "file", "test.mp3", "audio/mpeg", "test audio content".getBytes());

        // Set field values using reflection
        ReflectionTestUtils.setField(audioService, "maxDurationHours", 4);
        ReflectionTestUtils.setField(audioService, "maxFileSizeMb", 200);
        ReflectionTestUtils.setField(audioService, "allowedFormats", "mp3,wav,m4a");
    }

    @Test
    void uploadAudio_ShouldReturnResponse_WhenValidFile() {
        // Given
        when(userService.findByEmail(user.getEmail())).thenReturn(user);
        when(audioStorageService.storeFile(any(), any())).thenReturn("stored-filename.mp3");
        when(audioRepository.save(any(Audio.class))).thenReturn(Audio.builder().id(UUID.randomUUID()).build());

        // When
        var result = audioService.uploadAudio(validAudioFile, user.getEmail());

        // Then
        assertNotNull(result);
        verify(audioRepository, times(1)).save(any(Audio.class));
    }

    @Test
    void uploadAudio_ShouldThrowException_WhenFileEmpty() {
        // Given
        MockMultipartFile emptyFile = new MockMultipartFile("file", "", "", new byte[0]);

        // When & Then
        assertThrows(InvalidFileException.class, 
                () -> audioService.uploadAudio(emptyFile, user.getEmail()));
    }

    @Test
    void uploadAudio_ShouldThrowException_WhenInvalidFormat() {
        // Given
        MockMultipartFile invalidFile = new MockMultipartFile(
                "file", "test.txt", "text/plain", "test content".getBytes());

        // When & Then
        assertThrows(InvalidFileException.class, 
                () -> audioService.uploadAudio(invalidFile, user.getEmail()));
    }
}
