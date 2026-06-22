package com.venueelite.app.dto.bookings;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckVenueAvailabilityResponse {
    private boolean isAvailable;
}