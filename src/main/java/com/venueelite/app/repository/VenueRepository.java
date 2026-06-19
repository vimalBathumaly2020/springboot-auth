package com.venueelite.app.repository;

import org.springframework.stereotype.Repository;

import com.venueelite.app.entity.Venue;
import com.venueelite.app.enums.VenueStatus;
import com.venueelite.app.enums.VenueType;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.venueelite.app.enums.VenueStatus;
import com.venueelite.app.enums.VenueType;
// import com.venueelite.app.enums.VenueDay;
import com.venueelite.app.entity.Venue;

@Repository
public interface VenueRepository extends MongoRepository<Venue,String>{
    List<Venue> findByHostId(String hostId);
    List<Venue> findByVenueStatus(VenueStatus venueStatus);
    List<Venue> findByVenueType(VenueType venueType);
    List<Venue> findByAddressCity(String city);
}

