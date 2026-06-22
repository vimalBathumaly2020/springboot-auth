package com.venueelite.app.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.venueelite.app.entity.Booking;
import com.venueelite.app.enums.BookingStatus;

import java.time.LocalDate;


public interface BookingRepository extends MongoRepository<Booking, String> {
    boolean existsByVenueIdAndBookingDateAndStatus(String venueId, LocalDate bookingDate, BookingStatus bookingStatus); // BookingStatus.CONFIRMED
    Booking findFirstByVenueIdAndUserNameAndStatus(String venueId, String userName, BookingStatus bookingStatus); // BookingStatus.CONFIRMED
}
