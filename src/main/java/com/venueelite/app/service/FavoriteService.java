package com.venueelite.app.service;

import com.venueelite.app.entity.Favorite;
import com.venueelite.app.entity.Venue;
import com.venueelite.app.repository.FavoriteRepository;
import com.venueelite.app.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final VenueRepository venueRepository;

    // ❤️ Toggle save/unsave
    public String toggleFavorite(String userId, String venueId) {
        Optional<Favorite> existing = favoriteRepository.findByUserIdAndVenueId(userId, venueId);
        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
            return "Removed from favorites";
        }
        Favorite favorite = Favorite.builder()
                .userId(userId)
                .venueId(venueId)
                .build();
        favoriteRepository.save(favorite);
        return "Added to favorites";
    }

    // 📋 List saved venues (returns full Venue objects)
    public List<Venue> getSavedVenues(String userId) {
        return favoriteRepository.findByUserId(userId)
                .stream()
                .map(fav -> venueRepository.findById(fav.getVenueId()).orElse(null))
                .filter(venue -> venue != null)
                .collect(Collectors.toList());
    }

    // 🗑️ Cascade delete when venue is removed
    public void deleteByVenueId(String venueId) {
        favoriteRepository.deleteByVenueId(venueId);
    }
}