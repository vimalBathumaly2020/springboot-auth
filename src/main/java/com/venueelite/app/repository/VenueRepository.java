package com.venueelite.app.repository;


import com.venueelite.app.entity.Venue;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VenueRepository extends MongoRepository<Venue, String> {
    // search queries go through MongoTemplate — nothing needed here
}