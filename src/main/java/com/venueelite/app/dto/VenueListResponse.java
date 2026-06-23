package com.venueelite.app.dto;

import com.venueelite.app.entity.Address;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.venueelite.app.enums.VenueStatus;
import com.venueelite.app.enums.VenueType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for venue list / search results.
 *
 * Flutter Venue.fromJson field mapping:
 *   _id / id        → id
 *   title           → name
 *   venueType       → spaceType  (normalised in Flutter)
 *   capacity        → capacity
 *   pricePerHour    → pricePerHour
 *   rating          → rating
 *   reviewCount     → reviewsCount
 *   address { street, city, state, country } → location / city
 *   amenities       → amenities  (used by amenity filter chips)
 *   images          → imageUrl (first) + galleryImages (all)
 *   status          → isVerified (PUBLISHED → true)
 *   description     → description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VenueListResponse {

    private String           id;
    private String           title;
    private VenueType        venueType;
    private int              capacity;
    private double           pricePerHour;
    private double           rating;
    private int              reviewCount;

    // ── Full address object (not just city) ──────────────────────────────────
    // Flutter fromJson reads address.street, address.city, etc.
    private Address          address;

    // ── Amenities list ───────────────────────────────────────────────────────
    // Flutter amenity filter chips iterate this array.
    private List<String>     amenities;

    // ── Images ───────────────────────────────────────────────────────────────
    private List<String>     images;

    // Flutter treats "PUBLISHED" as isVerified = true
    // @JsonProperty ensures JSON key stays "status" regardless of field name
    @JsonProperty("status")
    private VenueStatus      status;

    // ── Optional extras ─────────────────────────────────────────────────────
    private String           description;
    private Double           latitude;
    private Double           longitude;
}