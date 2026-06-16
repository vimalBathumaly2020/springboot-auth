package com.venueelite.app.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String fullName;
    private String profileImage;
}