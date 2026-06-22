package com.venueelite.app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



import com.venueelite.app.dto.VenueDto;
import com.venueelite.app.entity.Venue;
import com.venueelite.app.enums.VenueStatus;
import com.venueelite.app.service.VenueService;

import com.venueelite.app.entity.Favorite;
import com.venueelite.app.service.FavoriteService;



import java.util.List;
import java.util.Optional;




@RestController
@RequestMapping("/api/venue")
@RequiredArgsConstructor
public class VenueController {
    private final VenueService venueService;
    private final FavoriteService favoriteService;
    @GetMapping("/all")
    public ResponseEntity<List<Venue>> getAllVenues() {
        return ResponseEntity.ok(venueService.getAllVenues());
    }
    @GetMapping("/host/{hostId}")
    public ResponseEntity<List<Venue>> getVenueByHostId(@PathVariable String hostId){
        return ResponseEntity.ok(venueService.getVenuesByHostId(hostId));
    }
    @GetMapping("/city/{city}")
    public ResponseEntity<List<Venue>> getVenuesByAddressCity(@PathVariable String city) {
        return ResponseEntity.ok(venueService.getVenuesByAddressCity(city));
    }
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Venue>> getVenuesByVenueStatus(@PathVariable VenueStatus status) {
        return ResponseEntity.ok(venueService.getVenuesByVenueStatus(status));
    }
    @PostMapping("/create-venue")
    public ResponseEntity<Venue> createVenue(@RequestBody VenueDto dto) {
        Venue saveCreateVenue = venueService.createVenue(dto);
        return ResponseEntity.status(201).body(saveCreateVenue);
    }
    @PutMapping("/update-venue/{venueId}")
    public ResponseEntity<Venue> updateVenue(@PathVariable String venueId, @RequestBody VenueDto dto) {
        Venue updatedVenue = venueService.updateVenue(venueId,dto);
        if(updatedVenue==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedVenue);
    }
    @DeleteMapping("/delete/{venueId}")
    public ResponseEntity<String> deleteVenue(@PathVariable String venueId){
        Optional<Venue> wantedVenue = venueService.getVenuesById(venueId);
        if(!wantedVenue.isPresent()){
            return ResponseEntity.notFound().build();
        }
        venueService.deleteVenue(venueId);
        favoriteService.deleteByVenueId(venueId);
        return ResponseEntity.ok("Venue delete successfully");
    }
}
