package com.venueelite.app.entity;

import lombok.Data;

@Data
public class AvailabilitySlot {
    private String day;
    private String openTime;
    private String closeTime;
}