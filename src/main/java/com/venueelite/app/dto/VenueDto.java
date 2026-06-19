package com.venueelite.app.dto;

import com.venueelite.app.entity.Address;
import com.venueelite.app.entity.Availability;
import com.venueelite.app.enums.VenueType;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VenueDto {
    private String hostId;
    private String title;
    private String description;
    private VenueType venueType;
    private Integer capacity;
    private Double pricePerHour;
    private Address address;
    private Double longitude;
    private Double latitude;
    private List<String> amenities;
    private List<String> images;
    private List<Availability> isAvailable;
    // private VenueStatus venueStatus;
    // private Double rating;
    // private Integer reviewCount;
    // private String rejectReason;
    // private LocalDateTime createAt;
    // private LocalDateTime updateAt;S



}
