package com.venueelite.app.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ApiErrorResponse {

    private String message;
    private int status;
    private LocalDateTime timestamp;
    private String path;
}