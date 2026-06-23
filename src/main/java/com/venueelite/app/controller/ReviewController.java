package com.venueelite.app.controller;

import com.cloudinary.api.ApiResponse;
import com.venueelite.app.dto.AuthResponse;
import com.venueelite.app.dto.ReviewRequest;
import com.venueelite.app.dto.ReviewResponse;
import com.venueelite.app.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // -------------------------------------------------------------------------
    // POST /api/v1/reviews
    // Auth: USER
    // Validates booking ownership, COMPLETED status, and one-review-per-booking
    // -------------------------------------------------------------------------
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @AuthenticationPrincipal AuthResponse principal,
            @Valid @RequestBody ReviewRequest request) {

        ReviewResponse response = reviewService.createReview(principal.getId(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Review submitted successfully", response));
    }

    // -------------------------------------------------------------------------
    // GET /api/v1/reviews/venue/{venueId}?page=0&size=10
    // Auth: Public
    // -------------------------------------------------------------------------
    @GetMapping("/venue/{venueId}")
    public <PagedResponse> ResponseEntity<ApiResponse<PagedResponse<ReviewResponse>>> getVenueReviews(
            @PathVariable String venueId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewResponse> reviewPage = reviewService.getVenueReviews(venueId, pageable);

        PagedResponse<ReviewResponse> paged = PagedResponse.<ReviewResponse>builder()
                .data(reviewPage.getContent())
                .page(reviewPage.getNumber())
                .size(reviewPage.getSize())
                .totalElements(reviewPage.getTotalElements())
                .totalPages(reviewPage.getTotalPages())
                .build();

        return ResponseEntity.ok(ApiResponse.success("Reviews retrieved", paged));
    }
}
