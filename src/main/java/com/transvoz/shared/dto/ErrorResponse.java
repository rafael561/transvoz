package com.transvoz.shared.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ErrorResponse {
    private String message;
    private Map<String, String> details;
    private LocalDateTime timestamp;
}
