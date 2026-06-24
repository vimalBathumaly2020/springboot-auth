package com.venueelite.app.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reviews")
@CompoundIndexes({
        @CompoundIndex(name = "venue_idx",      def = "{'venueId': 1}"),
        @CompoundIndex(name = "user_idx",       def = "{'userId': 1}"),
        // One review per booking — enforced at DB level
        @CompoundIndex(name = "booking_unique", def = "{'bookingId': 1}", unique = true)
})
public class Review {

    @Id
    private String id;

    private String userId;    // FK → users
    private String venueId;   // FK → venues
    private String bookingId; // FK → bookings (unique — one review per booking)

    private Integer rating;   // 1–5
    private String comment;   // optional, max 1000 chars

    @CreatedDate
    private LocalDateTime createdAt;
}
