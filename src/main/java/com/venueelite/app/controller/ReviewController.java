package com.venueelite.app.controller;

import com.venueelite.app.dto.ReviewRequest;
import com.venueelite.app.dto.ReviewResponse;
import com.venueelite.app.security.UserPrincipal;
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
    // POST /api/reviews
    // Auth: USER
    // -------------------------------------------------------------------------
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReviewResponse> createReview(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ReviewRequest request) {

        // UserPrincipal has no getId() — use getUser().getId()
        String userId = principal.getUser().getId();
        ReviewResponse response = reviewService.createReview(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // -------------------------------------------------------------------------
    // GET /api/reviews/venue/{venueId}?page=0&size=10
    // Auth: Public
    // -------------------------------------------------------------------------
    @GetMapping("/venue/{venueId}")
    public ResponseEntity<Page<ReviewResponse>> getVenueReviews(
            @PathVariable String venueId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewResponse> reviews = reviewService.getVenueReviews(venueId, pageable);
        return ResponseEntity.ok(reviews);
    }
}
