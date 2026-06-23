package com.venueelite.app.service;

import com.venueelite.app.dto.ReviewRequest;
import com.venueelite.app.dto.ReviewResponse;
import com.venueelite.app.entity.Review;
import com.venueelite.app.entity.User;
import com.venueelite.app.repository.ReviewRepository;
import com.venueelite.app.repository.UserRepository;
import com.venueelite.app.repository.VenueRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final BookingRepository   bookingRepository;
    private final VenueRepository venueRepository;
    private final UserRepository userRepository;

    // -------------------------------------------------------------------------
    // POST /reviews  (USER only)
    // -------------------------------------------------------------------------
    @Override
    @Transactional
    public ReviewResponse createReview(String userId, ReviewRequest request) {

        ObjectId userObjId    = new ObjectId(userId);
        ObjectId bookingObjId = new ObjectId(request.getBookingId());
        ObjectId venueObjId   = new ObjectId(request.getVenueId());

        // 1. Booking must exist and belong to the requesting user
        Booking booking = bookingRepository.findById(bookingObjId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getUserId().equals(userObjId)) {
            throw new UnauthorizedException("You can only review your own bookings");
        }

        // 2. Booking must be COMPLETED
        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new ValidationException("Reviews are only allowed for completed bookings");
        }

        // 3. venueId in request must match the booking
        if (!booking.getVenueId().equals(venueObjId)) {
            throw new ValidationException("venueId does not match this booking");
        }

        // 4. One review per booking (enforced by unique index too, but fail-fast here)
        if (reviewRepository.existsByBookingId(bookingObjId)) {
            throw new ValidationException("A review has already been submitted for this booking");
        }

        // 5. Persist review
        Review review = Review.builder()
                .userId(userObjId)
                .venueId(venueObjId)
                .bookingId(bookingObjId)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Review saved = reviewRepository.save(review);
        log.info("Review {} created for venue {} by user {}", saved.getId(), venueObjId, userObjId);

        // 6. Recalculate venue rating
        recalculateAndSaveVenueRating(venueObjId);

        // 7. Fetch reviewer details for response
        User reviewer = userRepository.findById(userObjId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return toResponse(saved, reviewer);
    }

    // -------------------------------------------------------------------------
    // GET /reviews/venue/{venueId}  (Public)
    // -------------------------------------------------------------------------
    @Override
    public Page<ReviewResponse> getVenueReviews(String venueId, Pageable pageable) {

        ObjectId venueObjId = new ObjectId(venueId);

        // Verify venue exists
        if (!venueRepository.existsById(venueObjId)) {
            throw new ResourceNotFoundException("Venue not found");
        }

        Page<Review> reviews = reviewRepository
                .findByVenueIdOrderByCreatedAtDesc(venueObjId, pageable);

        return reviews.map(review -> {
            User reviewer = userRepository.findById(review.getUserId()).orElse(null);
            return toResponse(review, reviewer);
        });
    }

    // -------------------------------------------------------------------------
    // DELETE /reviews/{id}  (ADMIN only)
    // -------------------------------------------------------------------------
    @Override
    @Transactional
    public void deleteReview(String reviewId) {

        ObjectId reviewObjId = new ObjectId(reviewId);

        Review review = reviewRepository.findById(reviewObjId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        ObjectId venueObjId = review.getVenueId();

        reviewRepository.deleteById(reviewObjId);
        log.info("Admin deleted review {} for venue {}", reviewObjId, venueObjId);

        // Recalculate venue rating after removal
        recalculateAndSaveVenueRating(venueObjId);
    }

    // -------------------------------------------------------------------------
    // Rating recalculation  — called after every create / delete
    // -------------------------------------------------------------------------

    /**
     * Recomputes the venue's average rating and review count using a MongoDB
     * aggregation pipeline, then persists both fields to the venues collection.
     * If no reviews remain, rating is set to 0.0 and reviewCount to 0.
     */
    private void recalculateAndSaveVenueRating(ObjectId venueId) {

        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found during rating recalculation"));

        List<VenueRatingProjection> result = reviewRepository.computeVenueRating(venueId);

        if (result.isEmpty()) {
            // All reviews removed — reset to zero
            venue.setRating(0.0);
            venue.setReviewCount(0);
        } else {
            VenueRatingProjection stats = result.get(0);
            // Round to 1 decimal place for display
            double rounded = Math.round(stats.getAvgRating() * 10.0) / 10.0;
            venue.setRating(rounded);
            venue.setReviewCount(stats.getReviewCount().intValue());
        }

        venueRepository.save(venue);
        log.info("Venue {} rating updated to {} ({} reviews)",
                venueId, venue.getRating(), venue.getReviewCount());
    }

    // -------------------------------------------------------------------------
    // Mapper
    // -------------------------------------------------------------------------
    private ReviewResponse toResponse(Review review, User reviewer) {
        return ReviewResponse.builder()
                .id(review.getId().toHexString())
                .userId(review.getUserId().toHexString())
                .userName(reviewer != null ? reviewer.getFullName() : "Unknown")
                .userProfileImage(reviewer != null ? reviewer.getProfileImage() : null)
                .venueId(review.getVenueId().toHexString())
                .bookingId(review.getBookingId().toHexString())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
