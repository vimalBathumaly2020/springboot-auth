package com.venueelite.app.service.booking;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.venueelite.app.dto.bookings.BookingRequest;
import com.venueelite.app.entity.Booking;
import com.venueelite.app.enums.BookingStatus;
import com.venueelite.app.repository.BookingRepository;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final Authentication authentication;

    @Override
    public boolean checkVenueAvailability(String venueId, LocalDate bookingDate) {
        boolean isAvailable = !bookingRepository.existsByVenueIdAndBookingDateAndStatus(venueId, bookingDate, BookingStatus.CONFIRMED);
        return isAvailable;
    }

    @Override
    public Booking checkVenueIsOnUpcomingList(String venueId, String userName)
    {
        Booking upcomingBookingData = bookingRepository.findFirstByVenueIdAndUserNameAndStatus(venueId, userName, BookingStatus.CONFIRMED);
        return upcomingBookingData;
    }

    @Override
    public Booking bookSpace(BookingRequest bookingRequest)
    {
        Booking booking = Booking.builder()
                            .bookingDate(bookingRequest.getBookingDate())
                            .userName(authentication.getName())
                            .venueId(bookingRequest.getVenueId())
                            .hours(bookingRequest.getHours())
                            .guessCount(bookingRequest.getGuessCount())
                            .status(BookingStatus.CONFIRMED)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();

        bookingRepository.save(booking);

        Booking bookingData = booking;
        return bookingData;
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
