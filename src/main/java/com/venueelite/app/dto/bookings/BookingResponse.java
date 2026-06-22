package com.venueelite.app.dto.bookings;

import com.venueelite.app.entity.Booking;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingResponse {
    private Booking bookingData;
}
