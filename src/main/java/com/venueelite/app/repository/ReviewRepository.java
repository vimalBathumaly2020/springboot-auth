package com.venueelite.app.repository;

import com.venueelite.app.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, String> {

    /** All reviews for a venue, newest first */
    Page<Review> findByVenueIdOrderByCreatedAtDesc(String venueId, Pageable pageable);

    /** Guard: has this booking already been reviewed? */
    boolean existsByBookingId(String bookingId);

    /**
     * Aggregation: compute average rating and review count for a venue in one query.
     * Returns empty list when venue has no reviews.
     */
    @Aggregation(pipeline = {
            "{ '$match': { 'venueId': ?0 } }",
            "{ '$group': { '_id': '$venueId', 'avgRating': { '$avg': '$rating' }, 'reviewCount': { '$sum': 1 } } }"
    })
    List<VenueRatingProjection> computeVenueRating(String venueId);

    // ---- projection --------------------------------------------------------
    interface VenueRatingProjection {
        Double getAvgRating();
        Long getReviewCount();
    }

}
