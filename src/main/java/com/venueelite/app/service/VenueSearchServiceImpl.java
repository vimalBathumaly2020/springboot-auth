package com.venueelite.app.service;


import com.venueelite.app.enums.VenueStatus;
import com.venueelite.app.dto.VenueSearchRequest;
import com.venueelite.app.dto.VenueListResponse;
import com.venueelite.app.entity.Venue;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VenueSearchServiceImpl implements VenueSearchService {

    private final MongoTemplate mongoTemplate;

    // ── KEYWORD SEARCH ───────────────────────────────────────
    @Override
    public Page<VenueListResponse> search(String keyword, int page, int size) {
        Criteria criteria = Criteria.where("status").is(VenueStatus.PUBLISHED)
                .orOperator(
                        Criteria.where("title").regex(keyword, "i"),
                        Criteria.where("description").regex(keyword, "i")
                );

        return runQuery(criteria,
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    // ── FILTER ───────────────────────────────────────────────
    @Override
    public Page<VenueListResponse> filter(VenueSearchRequest req) {
        Criteria criteria = Criteria.where("status").is(VenueStatus.PUBLISHED);

        if (req.getVenueType() != null)
            criteria.and("venueType").is(req.getVenueType().name());

        if (req.getMinCapacity() != null)
            criteria.and("capacity").gte(req.getMinCapacity());

        if (req.getMaxPrice() != null)
            criteria.and("pricePerHour").lte(req.getMaxPrice());

        if (req.getCity() != null && !req.getCity().isBlank())
            criteria.and("address.city").regex(req.getCity(), "i");

        if (req.getAmenities() != null && !req.getAmenities().isEmpty())
            criteria.and("amenities").all(req.getAmenities());

        Sort sort = req.getSortDir().equalsIgnoreCase("asc")
                ? Sort.by(req.getSortBy()).ascending()
                : Sort.by(req.getSortBy()).descending();

        return runQuery(criteria, PageRequest.of(req.getPage(), req.getSize(), sort));
    }

    // ── SHARED ───────────────────────────────────────────────
    private Page<VenueListResponse> runQuery(Criteria criteria, Pageable pageable) {
        Query query = new Query(criteria).with(pageable);

        long total = mongoTemplate.count(new Query(criteria), Venue.class);
        List<VenueListResponse> content = mongoTemplate.find(query, Venue.class)
                .stream()
                .map(v -> new VenueListResponse(
                        v.getId(),
                        v.getTitle(),
                        v.getVenueType(),
                        v.getCapacity(),
                        v.getPricePerHour(),
                        v.getRating(),
                        v.getReviewCount(),
                        v.getAddress() != null ? v.getAddress().getCity() : null,
                        v.getImages()
                ))
                .toList();

        return new PageImpl<>(content, pageable, total);
    }
}