package com.venueelite.app.dto.bookings;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    @NotNull private LocalDate bookingDate;
    @NotBlank private String venueId;
    @NotBlank private int hours;
    @NotBlank private int guessCount;
}
