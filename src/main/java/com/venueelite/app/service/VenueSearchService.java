package com.venueelite.app.service;


import com.venueelite.app.dto.VenueSearchRequest;
import com.venueelite.app.dto.VenueListResponse;
import org.springframework.data.domain.Page;

public interface VenueSearchService {
    Page<VenueListResponse> search(String keyword, int page, int size);
    Page<VenueListResponse> filter(VenueSearchRequest request);
}