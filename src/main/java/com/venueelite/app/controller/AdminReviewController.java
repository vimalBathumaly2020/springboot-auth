package com.venueelite.app.controller;

import com.cloudinary.api.ApiResponse;
import com.venueelite.app.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/reviews")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminReviewController {
    private final ReviewService reviewService;

    // -------------------------------------------------------------------------
    // DELETE /api/v1/admin/reviews/{id}
    // Auth: ADMIN only
    // Deletes review and triggers automatic venue rating recalculation
    // -------------------------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable String id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok(ApiResponse.success("Review removed and venue rating updated", null));
    }
}
