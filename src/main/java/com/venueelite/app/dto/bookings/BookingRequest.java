package com.venueelite.app.dto.bookings;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BookingRequest {
    @NotBlank
    private LocalDate bookingDate;

    @NotBlank
    private String userId;
    
    @NotBlank
    private String venueId;

    @NotBlank
    private String hours;
    
    @NotBlank
    private String guessCount;
}
