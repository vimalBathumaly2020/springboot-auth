package com.venueelite.app.entity;

import com.venueelite.app.enums.VenueStatus;
import com.venueelite.app.enums.VenueType;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "venues")   // must match your Atlas collection name exactly
public class Venue {

    @Id
    private String id;

    private String hostId;
    private String title;
    private String description;
    private VenueType venueType;
    private Integer capacity;
    private BigDecimal pricePerHour;
    private Address address;
    private List<String> amenities;
    private List<String> images;
    private List<AvailabilitySlot> availabilitySchedule;
    private VenueStatus status;
    private Double rating;
    private Integer reviewCount;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}