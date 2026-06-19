package com.venueelite.app.entity;

import com.venueelite.app.enums.VenueDay;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Availability {
    private VenueDay day; // enum VenueDay
    private String openTime;
    private String closeTime;
    private boolean isAvailable;
}
