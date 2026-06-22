package com.venueelite.app.service.booking;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.venueelite.app.dto.bookings.BookingRequest;
import com.venueelite.app.dto.bookings.BookingResponse;
// import com.venueelite.app.dto.bookings.CheckVenueAvailabilityRequest;
import com.venueelite.app.dto.bookings.CheckVenueAvailabilityResponse;
import com.venueelite.app.entity.Booking;
import com.venueelite.app.enums.BookingStatus;
import com.venueelite.app.repository.BookingRepository;

import jakarta.validation.constraints.NotBlank;



@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    @Override
    public CheckVenueAvailabilityResponse checkVenueAvailability(String venueId, LocalDate bookingDate) {
        // Implementation for checking venue availability

        // final String venueId = request.getVenueId();
        // final LocalDate bookingDate = request.getBookingDate();

        boolean isAvailable = true; // bookingRepository.findByVenueIdAndBookingDateAndStatusNotTrue(venueId, bookingDate, BookingStatus.CONFIRMED); // Claude said this is wrong.

        return CheckVenueAvailabilityResponse.builder().isAvailable(isAvailable).build();
    }

    // @Override
    // public BookingResponse bookVenue(BookingRequest request) {
    //     // Implementation for booking a venue
    // }

    // private CheckVenueAvailabilityResponse checkVenueAvailabilityResponse(boolean isOccupied)
    // {
    //     return CheckVenueAvailabilityResponse.builder().isOccupied(isOccupied).build();
    // }
}
