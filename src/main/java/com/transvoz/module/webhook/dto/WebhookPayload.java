package com.transvoz.module.webhook.dto;

import lombok.Data;

import java.util.List;

@Data
public class WebhookPayload {
    private String jobId;
    private String status;
    private String text;
    private String language;
    private Double confidence;
    private Integer processingTime;
    private List<WebhookSpeaker> speakers;
    private String errorMessage;

    @Data
    public static class WebhookSpeaker {
        private String speaker;
        private Double startTime;
        private Double endTime;
        private String text;
        private Double confidence;
    }
}
