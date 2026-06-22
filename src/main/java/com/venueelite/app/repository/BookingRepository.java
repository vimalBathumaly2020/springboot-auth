package com.venueelite.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.venueelite.app.entity.Booking;
import com.venueelite.app.enums.BookingStatus;

import java.time.LocalDate;


public interface BookingRepository extends MongoRepository<Booking, String> {
    // boolean findByVenueIdAndBookingDateAndStatusNotTrue(String venueId, LocalDate bookingDate, BookingStatus bookingStatus);
    List<Booking> findAllByUserId(String userId);
    List<Booking> findAllByVenueId(String venueId);
}
