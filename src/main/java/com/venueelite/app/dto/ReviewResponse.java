package com.venueelite.app.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewResponse {

    private String id;
    private String userId;
    private String userName;        // denormalized for display
    private String userProfileImage;
    private String venueId;
    private String bookingId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
