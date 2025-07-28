package com.transvoz.module.transcription;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transvoz.module.transcription.controller.TranscriptionController;
import com.transvoz.module.transcription.dto.TranscriptionRequest;
import com.transvoz.module.transcription.service.TranscriptionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TranscriptionController.class)
class TranscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TranscriptionService transcriptionService;

    @Test
    @WithMockUser
    void startTranscription_ShouldReturnCreated_WhenValidRequest() throws Exception {
        TranscriptionRequest request = new TranscriptionRequest();
        request.setAudioId(UUID.randomUUID());
        request.setEnableDiarization(true);
        request.setMaxSpeakers(6);

        mockMvc.perform(post("/api/v1/transcriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}
