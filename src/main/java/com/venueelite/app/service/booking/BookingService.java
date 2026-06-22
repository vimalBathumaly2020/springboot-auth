package com.venueelite.app.service.booking;

import java.time.LocalDate;

import com.venueelite.app.dto.bookings.BookingRequest;
import com.venueelite.app.entity.Booking;
 

public interface BookingService {
    boolean checkVenueAvailability(String venueId, LocalDate bookingDate);
    Booking checkVenueIsOnUpcomingList(String venueId, String userName);
    Booking bookSpace(BookingRequest bookingRequest);
}
