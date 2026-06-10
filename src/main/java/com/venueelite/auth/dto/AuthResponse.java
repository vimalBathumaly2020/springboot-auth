package com.venueelite.auth.dto;

import com.venueelite.auth.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private User user;
}