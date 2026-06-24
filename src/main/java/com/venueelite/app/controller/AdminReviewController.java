package com.venueelite.app.controller;

import com.venueelite.app.dto.MessageResponse;
import com.venueelite.app.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/reviews")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminReviewController {
    private final ReviewService reviewService;

    // -------------------------------------------------------------------------
    // DELETE /api/admin/reviews/{id}
    // Auth: ADMIN only — deletes review and auto-updates venue rating
    // -------------------------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteReview(@PathVariable String id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok(
                MessageResponse.builder()
                        .success(true)
                        .message("Review removed and venue rating updated")
                        .build()
        );
    }
}
