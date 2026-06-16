package com.venueelite.app.dto;

import com.venueelite.app.enums.VenueType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class VenueSearchRequest {
    private String q;                        // keyword
    private VenueType venueType;
    private Integer minCapacity;
    private BigDecimal maxPrice;
    private String city;
    private List<String> amenities;
    private String sortBy = "createdAt";     // rating | price | createdAt
    private String sortDir = "desc";
    private int page = 0;
    private int size = 10;
}