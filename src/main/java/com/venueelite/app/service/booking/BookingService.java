package com.venueelite.app.service.booking;

import java.time.LocalDate;

import com.venueelite.app.dto.bookings.*;

public interface BookingService {
    CheckVenueAvailabilityResponse checkVenueAvailability(String venueId, LocalDate bookingDate);
    // BookingResponse bookVenue(BookingRequest request);
}
