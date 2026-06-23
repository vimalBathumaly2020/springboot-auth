package com.venueelite.app.service;

import com.venueelite.app.dto.ReviewRequest;
import com.venueelite.app.dto.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewService {
    /**
     * Post a review for a COMPLETED booking.
     * Validates:
     *   - Booking exists and belongs to the authenticated user
     *   - Booking status is COMPLETED
     *   - No review already exists for this booking
     *   - venueId matches the booking's venueId
     * After saving, triggers venue rating recalculation.
     *
     * @param userId  authenticated user's ID
     * @param request review payload
     * @return saved ReviewResponse
     */
    ReviewResponse createReview(String userId, ReviewRequest request);

    /**
     * Fetch paginated reviews for a venue, ordered newest-first.
     *
     * @param venueId target venue
     * @param pageable pagination / sort
     * @return page of ReviewResponse
     */
    Page<ReviewResponse> getVenueReviews(String venueId, Pageable pageable);

    /**
     * Admin moderation: delete a review by ID and recalculate the venue rating.
     *
     * @param reviewId review to remove
     */
    void deleteReview(String reviewId);
}
