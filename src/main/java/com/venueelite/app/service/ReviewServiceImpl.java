package com.venueelite.app.service;

import com.venueelite.app.dto.ReviewRequest;
import com.venueelite.app.dto.ReviewResponse;
import com.venueelite.app.entity.Booking;
import com.venueelite.app.entity.Review;
import com.venueelite.app.entity.User;
import com.venueelite.app.entity.Venue;
import com.venueelite.app.enums.BookingStatus;
import com.venueelite.app.repository.BookingRepository;
import com.venueelite.app.repository.ReviewRepository;
import com.venueelite.app.repository.ReviewRepository.VenueRatingProjection;
import com.venueelite.app.repository.UserRepository;
import com.venueelite.app.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository  reviewRepository;
    private final BookingRepository bookingRepository;
    private final VenueRepository   venueRepository;
    private final UserRepository    userRepository;

    // -------------------------------------------------------------------------
    // POST /api/reviews  (USER only)
    // -------------------------------------------------------------------------
    @Override
    public ReviewResponse createReview(String userId, ReviewRequest request) {

        // 1. Booking must exist
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Booking not found"));

        // 2. Booking venueId must match the request
        //    NOTE: Booking.java has no userId field — ownership is via userName.
        //    venueId cross-check is the guard here.
        if (!booking.getVenueId().equals(request.getVenueId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "venueId does not match this booking");
        }

        // 3. Booking must be COMPLETED
        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Reviews are only allowed for completed bookings");
        }

        // 4. One review per booking
        if (reviewRepository.existsByBookingId(request.getBookingId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "A review has already been submitted for this booking");
        }

        // 5. Persist review
        Review review = Review.builder()
                .userId(userId)
                .venueId(request.getVenueId())
                .bookingId(request.getBookingId())
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Review saved = reviewRepository.save(review);
        log.info("Review {} created for venue {} by user {}", saved.getId(), request.getVenueId(), userId);

        // 6. Auto-recalculate venue rating
        recalculateVenueRating(request.getVenueId());

        // 7. Fetch user for response
        User reviewer = userRepository.findById(userId).orElse(null);

        return toResponse(saved, reviewer);
    }

    // -------------------------------------------------------------------------
    // GET /api/reviews/venue/{venueId}  (Public)
    // -------------------------------------------------------------------------
    @Override
    public Page<ReviewResponse> getVenueReviews(String venueId, Pageable pageable) {

        if (!venueRepository.existsById(venueId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue not found");
        }

        return reviewRepository
                .findByVenueIdOrderByCreatedAtDesc(venueId, pageable)
                .map(review -> {
                    User reviewer = userRepository.findById(review.getUserId()).orElse(null);
                    return toResponse(review, reviewer);
                });
    }

    // -------------------------------------------------------------------------
    // DELETE /api/admin/reviews/{id}  (ADMIN only)
    // -------------------------------------------------------------------------
    @Override
    public void deleteReview(String reviewId) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Review not found"));

        String venueId = review.getVenueId();
        reviewRepository.deleteById(reviewId);
        log.info("Admin deleted review {} for venue {}", reviewId, venueId);

        recalculateVenueRating(venueId);
    }

    // -------------------------------------------------------------------------
    // Auto-update venue rating — called after every create / delete
    // -------------------------------------------------------------------------
    private void recalculateVenueRating(String venueId) {

        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Venue not found during rating recalculation"));

        List<VenueRatingProjection> result = reviewRepository.computeVenueRating(venueId);

        if (result.isEmpty()) {
            venue.setRating(0.0);
            venue.setReviewCount(0);
        } else {
            VenueRatingProjection stats = result.get(0);
            double rounded = Math.round(stats.getAvgRating() * 10.0) / 10.0;
            venue.setRating(rounded);
            venue.setReviewCount(stats.getReviewCount().intValue());
        }

        venueRepository.save(venue);
        log.info("Venue {} rating updated → {} ({} reviews)",
                venueId, venue.getRating(), venue.getReviewCount());
    }

    // -------------------------------------------------------------------------
    // Mapper
    // -------------------------------------------------------------------------
    private ReviewResponse toResponse(Review review, User reviewer) {
        return ReviewResponse.builder()
                .id(review.getId())
                .userId(review.getUserId())
                .userName(reviewer != null ? reviewer.getFullName() : "Unknown")
                .userProfileImage(reviewer != null ? reviewer.getProfileImage() : null)
                .venueId(review.getVenueId())
                .bookingId(review.getBookingId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
