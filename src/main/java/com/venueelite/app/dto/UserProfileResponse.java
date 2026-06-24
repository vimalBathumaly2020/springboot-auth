package com.venueelite.app.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserProfileResponse {
    private String id;
    private String email;
    private String fullName;
    private String profileImage;
    private String role;
    private String accountStatus;
    private LocalDateTime createdAt;
}