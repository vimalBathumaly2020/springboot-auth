package com.venueelite.app.controller;
import com.venueelite.app.dto.bookings.BookingRequest;
import com.venueelite.app.entity.Booking;
import com.venueelite.app.service.booking.BookingService;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/availability")
    public ResponseEntity<Boolean> checkAvailability(@RequestParam String venueId, @RequestParam LocalDate bookingDate) {
        return ResponseEntity.ok(bookingService.checkVenueAvailability(venueId, bookingDate));
    }

    @GetMapping("/upcoming")
    @PreAuthorize("hasAnyRole('USER', 'HOST', 'ADMIN')")
    public ResponseEntity<Booking> checkUpcoming(Authentication auth, @RequestParam String venueId) {
        return ResponseEntity.ok(bookingService.checkVenueIsOnUpcomingList(venueId, auth.getName()));
    }

    @PostMapping("/book")
    @PreAuthorize("hasAnyRole('USER', 'HOST', 'ADMIN')")
    public ResponseEntity<Booking> bookSpace(@RequestBody BookingRequest bookingRequest) {
        return ResponseEntity.ok(bookingService.bookSpace(bookingRequest));
    }
}
