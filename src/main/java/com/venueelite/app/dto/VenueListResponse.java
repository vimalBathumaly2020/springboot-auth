package com.venueelite.app.dto;

import com.venueelite.app.enums.VenueType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class VenueListResponse {
    private String id;
    private String title;
    private VenueType venueType;
    private Integer capacity;
    private BigDecimal pricePerHour;
    private Double rating;
    private Integer reviewCount;
    private String city;
    private List<String> images;
}