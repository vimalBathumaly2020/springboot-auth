package com.venueelite.app.controller;

import com.venueelite.app.dto.VenueSearchRequest;
import com.venueelite.app.dto.VenueListResponse;
import com.venueelite.app.service.VenueSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/venues")
@RequiredArgsConstructor
public class VenueSearchController {

    private final VenueSearchService venueSearchService;

    // GET /api/v1/venues/search?q=rooftop
    @GetMapping("/search")
    public ResponseEntity<Page<VenueListResponse>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(venueSearchService.search(q, page, size));
    }

    // GET /api/v1/venues/filter?venueType=STUDIO&city=KL&maxPrice=100
    @GetMapping("/filter")
    public ResponseEntity<Page<VenueListResponse>> filter(
            @ModelAttribute VenueSearchRequest request) {

        return ResponseEntity.ok(venueSearchService.filter(request));
    }
}