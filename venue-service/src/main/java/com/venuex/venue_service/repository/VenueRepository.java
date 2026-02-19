package com.venuex.venue_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.venuex.venue_service.entities.Venue;

public interface VenueRepository extends JpaRepository<Venue, Integer> {
    boolean existsByName(String name);
}
