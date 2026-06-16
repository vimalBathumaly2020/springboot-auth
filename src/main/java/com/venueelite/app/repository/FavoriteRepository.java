package com.venueelite.app.repository;

import com.venueelite.app.entity.Favorite;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends MongoRepository<Favorite, String> {

    List<Favorite> findByUserId(String userId);

    Optional<Favorite> findByUserIdAndVenueId(String userId, String venueId);

    void deleteByVenueId(String venueId);

    boolean existsByUserIdAndVenueId(String userId, String venueId);
}