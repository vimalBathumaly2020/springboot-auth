package com.venueelite.app.repository;

import com.venueelite.app.entity.Review;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


public interface ReviewRepository extends MongoRepository <Review, ObjectId> {

    /** All reviews for a venue, newest first (pagination handled by caller) */
    Page<Review> findByVenueIdOrderByCreatedAtDesc(ObjectId venueId, Pageable pageable);

    /** Guard: has this booking already been reviewed? */
    boolean existsByBookingId(ObjectId bookingId);

    /** Guard: check ownership before admin delete (optional extra safety) */
    Optional<Review> findByIdAndVenueId(ObjectId id, ObjectId venueId);

    /**
     * Aggregation: compute average rating and review count for a venue.
     * Returns a single-element list; use .stream().findFirst() on the result.
     *
     * Shape: { avgRating: Double, reviewCount: Long }
     */
    @Aggregation(pipeline = {
            "{ '$match': { 'venueId': ?0 } }",
            "{ '$group': { '_id': '$venueId', 'avgRating': { '$avg': '$rating' }, 'reviewCount': { '$sum': 1 } } }"
    })
    java.util.List<VenueRatingProjection> computeVenueRating(ObjectId venueId);

    // ---- projection --------------------------------------------------------
    interface VenueRatingProjection {
        Double getAvgRating();
        Long getReviewCount();
    }
}
