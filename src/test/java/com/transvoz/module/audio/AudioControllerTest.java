package com.transvoz.module.audio;

import com.transvoz.module.audio.controller.AudioController;
import com.transvoz.module.audio.service.AudioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AudioController.class)
class AudioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AudioService audioService;

    @Test
    @WithMockUser
    void uploadAudio_ShouldReturnCreated_WhenValidFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.mp3", "audio/mpeg", "test content".getBytes());

        mockMvc.perform(multipart("/api/v1/audio/upload").file(file))
                .andExpected(status().isCreated());
    }

    @Test
    @WithMockUser
    void getUserAudios_ShouldReturnOk_WhenAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/audio"))
                .andExpected(status().isOk());
    }
}
