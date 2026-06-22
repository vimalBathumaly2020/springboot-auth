package com.venueelite.app.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.venueelite.app.enums.VenueStatus;
import com.venueelite.app.enums.VenueType;

import org.springframework.data.mongodb.core.index.Indexed;
import java.time.LocalDateTime;
import java.util.List;

// imort venue enum and entity related =====================
import com.venueelite.app.enums.VenueType;
import com.venueelite.app.enums.VenueStatus;
// =========================================================

@Document(collection="venues")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Venue {
    @Id
    private String id;
    private String hostId;
    private String title;
    private String description;    
    private VenueType venueType; //enum VenueType
    private Integer capacity;
    private Double pricePerHour;
    private Address address; // nested bject, link with Address.java
    private Double longitude;
    private Double latitude;
    private List<String> amenities; // array of String
    private List<String> images; // array of STring 
    private List<Availability> isAvailable; // array of objects
    private VenueStatus venueStatus; // enum VenueStatus
    private Double rating;
    private Integer reviewCount;
    private String rejectReason;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private String createdBy;
    private String updatedBy;

}

