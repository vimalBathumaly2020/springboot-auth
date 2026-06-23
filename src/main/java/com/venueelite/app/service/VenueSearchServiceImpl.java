package com.venueelite.app.service;

import com.venueelite.app.dto.VenueListResponse;
import com.venueelite.app.dto.VenueSearchRequest;
import com.venueelite.app.entity.Venue;
import com.venueelite.app.enums.VenueStatus;
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

    // ── KEYWORD SEARCH ───────────────────────────────────────────────────────
    @Override
    public Page<VenueListResponse> search(String keyword, int page, int size) {
        Criteria criteria = Criteria.where("VenueStatus").is(VenueStatus.PUBLISHED.name())
                .orOperator(
                        Criteria.where("title").regex(keyword, "i"),
                        Criteria.where("description").regex(keyword, "i"),
                        Criteria.where("address.city").regex(keyword, "i")
                );

        return runQuery(criteria,
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    // ── FILTER ───────────────────────────────────────────────────────────────
    @Override
    public Page<VenueListResponse> filter(VenueSearchRequest req) {
        Criteria criteria = Criteria.where("VenueStatus").is(VenueStatus.PUBLISHED.name());

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

    // ── SHARED QUERY RUNNER ──────────────────────────────────────────────────
    private Page<VenueListResponse> runQuery(Criteria criteria, Pageable pageable) {
        Query query = new Query(criteria).with(pageable);

        long total = mongoTemplate.count(new Query(criteria), Venue.class);

        List<VenueListResponse> content = mongoTemplate.find(query, Venue.class)
                .stream()
                .map(this::toListResponse)
                .toList();

        return new PageImpl<>(content, pageable, total);
    }

    // ── ENTITY → DTO MAPPING ─────────────────────────────────────────────────
    private VenueListResponse toListResponse(Venue v) {
        return VenueListResponse.builder()
                .id(v.getId())
                .title(v.getTitle())
                .venueType(v.getVenueType())
                .capacity(v.getCapacity())
                .pricePerHour(v.getPricePerHour())
                .rating(v.getRating())
                .reviewCount(v.getReviewCount())
                .address(v.getAddress())   // Address is a plain POJO — passed directly
                .amenities(v.getAmenities())
                .images(v.getImages())
                .status(v.getVenueStatus())
                .description(v.getDescription())
                .latitude(v.getLatitude())
                .longitude(v.getLongitude())
                .build();
    }
}