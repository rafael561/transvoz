package com.transvoz.module.webhook;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.transvoz.module.webhook.controller.WebhookController;
import com.transvoz.module.webhook.dto.WebhookPayload;
import com.transvoz.module.webhook.service.WebhookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WebhookController.class)
class WebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WebhookService webhookService;

    @Test
    void receiveTranscriptionWebhook_ShouldReturnOk_WhenValidPayload() throws Exception {
        WebhookPayload payload = new WebhookPayload();
        payload.setJobId("job-123");
        payload.setStatus("completed");
        payload.setText("Hello world");

        mockMvc.perform(post("/api/v1/webhook/transcription")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk());
    }
}
