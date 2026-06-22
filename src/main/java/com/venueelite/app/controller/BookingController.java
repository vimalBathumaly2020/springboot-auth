package com.venueelite.app.controller;
import com.venueelite.app.service.booking.BookingService;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// import com.venueelite.app.dto.bookings.CheckVenueAvailabilityRequest;
import com.venueelite.app.dto.bookings.CheckVenueAvailabilityResponse;

@RestController
@RequestMapping("/api/v1/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/availability")
    public ResponseEntity<CheckVenueAvailabilityResponse> checkDateOccupied(@RequestParam String venueId, @RequestParam LocalDate bookingDate) {
        // return ResponseEntity.ok("any");
        return ResponseEntity.ok(bookingService.checkVenueAvailability(venueId, bookingDate));
    }

    // @GetMapping("/check-date-occupied")
    // public ResponseEntity<Boolean> checkDateOccupied() {
    //     return ResponseEntity.ok(true);
    // }
    
    // @PostMapping("/create")
    // @PreAuthorize("hasRole('USER')")
    // public ResponseEntity<String> createBooking() {
    //     return ResponseEntity.ok("Booking created successfully");
    // }
}
