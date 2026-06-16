package com.venueelite.app.controller;

import com.venueelite.app.entity.Venue;
import com.venueelite.app.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    // ❤️ Toggle favorite
    @PostMapping("/toggle")
    public ResponseEntity<String> toggleFavorite(
            @RequestParam String venueId,
            @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername(); // extracted from JWT
        return ResponseEntity.ok(favoriteService.toggleFavorite(userId, venueId));
    }

    // 📋 List saved venues
    @GetMapping("/{userId}")
    public ResponseEntity<List<Venue>> getSavedVenues(@PathVariable String userId) {
        return ResponseEntity.ok(favoriteService.getSavedVenues(userId));
    }
}