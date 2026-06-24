package com.venueelite.app.service;

import com.venueelite.app.dto.ReviewRequest;
import com.venueelite.app.dto.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    /**
     * Post a review for a COMPLETED booking.
     * Validates:
     *  - Booking exists and its venueId matches the request
     *  - Booking status is COMPLETED
     *  - No review already exists for this booking
     * After saving, auto-recalculates venue rating.
     *
     * @param userId  authenticated user's ID (from UserPrincipal)
     * @param request review payload
     */
    ReviewResponse createReview(String userId, ReviewRequest request);

    /**
     * Fetch paginated reviews for a venue, newest-first.
     */
    Page<ReviewResponse> getVenueReviews(String venueId, Pageable pageable);

    /**
     * Admin moderation: delete a review and recalculate venue rating.
     */
    void deleteReview(String reviewId);
}
