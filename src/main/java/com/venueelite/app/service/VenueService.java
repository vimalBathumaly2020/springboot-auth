package com.venueelite.app.service;

import com.venueelite.app.dto.VenueDto;
import com.venueelite.app.entity.Venue;
import com.venueelite.app.enums.VenueStatus;
import com.venueelite.app.enums.VenueType;
import com.venueelite.app.repository.VenueRepository;

import com.venueelite.app.entity.User;
import com.venueelite.app.repository.UserRepository;

import lombok.RequiredArgsConstructor;

// import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import org.springframework.security.access.AccessDeniedException;

import com.venueelite.app.dto.VenueDto;
import com.venueelite.app.entity.Venue;
import com.venueelite.app.enums.VenueStatus;
import com.venueelite.app.enums.VenueType;
import com.venueelite.app.repository.VenueRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VenueService{
    private final VenueRepository venueRepository;
    private final UserRepository userRepository;
    // private final FavoriteService favoriteService;


    public List<Venue> getAllVenues(){
        return venueRepository.findAll();
    }
    public Optional<Venue> getVenuesById(String id){
        return venueRepository.findById(id);
    }
    public List<Venue> getVenuesByHostId(String id){
        return venueRepository.findByHostId(id);
    }
    public List<Venue> getVenuesByVenueStatus(VenueStatus venueStatus){
        return venueRepository.findByVenueStatus(venueStatus);
    }
    public List<Venue> getVenuesByAddressCity(String addressCity){
        return venueRepository.findByAddressCity(addressCity);
    }

    public Venue createVenue(VenueDto dto,String userId){

        String hostName = userRepository.findById(userId).map(User::getFullName).orElse("unknown Host");
        Venue venue = Venue.builder()
                      .hostId(dto.getHostId())
                      .hostName(hostName)
                      .title(dto.getTitle())
                      .description(dto.getDescription())
                      .venueType(dto.getVenueType())
                      .capacity(dto.getCapacity())
                      .pricePerHour(dto.getPricePerHour())
                      .address(dto.getAddress())
                      .longitude(dto.getLongitude())
                      .latitude(dto.getLatitude())
                      .amenities(dto.getAmenities())
                      .images(dto.getImages())
                      .isAvailable(dto.getIsAvailable())
                      .VenueStatus(VenueStatus.PENDING)
                      .rating(0.0)
                      .reviewCount(0)
                      .rejectReason(null)
                      .createAt(LocalDateTime.now())
                      .updateAt(LocalDateTime.now())
                      .createdBy(userId)
                      .updatedBy(userId)
                      .build();
        return venueRepository.save(venue);
    }
    public Venue updateVenue(String id, VenueDto dto,String userId){
        Optional<Venue> existing = venueRepository.findById(id);
        
        if(existing.isPresent()){
            Venue venue = existing.get();
            if(!userId.equals(venue.getCreatedBy())){
                throw new AccessDeniedException("Your are not authorized to make a change for this venue");
            }
            String targetHostId = (dto.getHostId() != null) ? dto.getHostId():venue.getHostId();
            String hostName=userRepository.findById(targetHostId).map(User::getFullName).orElse("Unknown Host");
            venue.setTitle(dto.getTitle());
            venue.setHostId(targetHostId);
            venue.setHostName(hostName);
            venue.setDescription(dto.getDescription());
            venue.setVenueType(dto.getVenueType());
            venue.setCapacity(dto.getCapacity());
            venue.setPricePerHour(dto.getPricePerHour());
            venue.setAddress(dto.getAddress());
            venue.setLatitude(dto.getLatitude());
            venue.setLongitude(dto.getLongitude());
            venue.setAmenities(dto.getAmenities());
            venue.setImages(dto.getImages());
            venue.setIsAvailable(dto.getIsAvailable());
            venue.setUpdateAt(LocalDateTime.now());            
            venue.setUpdatedBy(userId);
            return venueRepository.save(venue);
        }
        return null;
    }
    public void deleteVenue(String id){
        venueRepository.deleteById(id);
    }

}