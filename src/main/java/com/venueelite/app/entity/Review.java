package com.venueelite.app.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
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
        @CompoundIndex(name = "venue_idx", def = "{'venueId': 1}"),
        @CompoundIndex(name = "user_idx", def = "{'userId': 1}"),
        @CompoundIndex(name = "booking_unique_idx",  def = "{'bookingId': 1}", unique = true) //Enforce one review per booking — prevents duplicate
})
public class Review {
    @Id
    private ObjectId id;

    /** The user who wrote this review */
    private ObjectId userId;

    /** The venue being reviewed */
    private ObjectId venueId;

    /**
     * The completed booking that qualifies the user to leave this review.
     * Unique index ensures at most one review per booking.
     */
    private ObjectId bookingId;

    /** Star rating 1–5 */
    private Integer rating;

    /** Optional free-text comment, max 1000 chars */
    private String comment;

    @CreatedDate
    private LocalDateTime createdAt;
}
