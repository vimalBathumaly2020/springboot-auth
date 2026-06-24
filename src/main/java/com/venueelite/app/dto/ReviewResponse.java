package com.venueelite.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    private String id;
    private String userId;
    private String userName;         // denormalized from User for display
    private String userProfileImage; // denormalized from User for display
    private String venueId;
    private String bookingId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
