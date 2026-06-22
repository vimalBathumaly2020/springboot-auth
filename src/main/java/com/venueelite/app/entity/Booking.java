package com.venueelite.app.entity;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import com.venueelite.app.enums.BookingStatus;

@Data
@Document(collection = "bookings") 
public class Booking {
    @Id
    private String id;

    private LocalDate bookingDate;
    private String userId;
    private String venueId;
    private BookingStatus status;
    private String hours;
    private String guessCount;
    
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
