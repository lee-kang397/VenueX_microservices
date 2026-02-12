package com.venuex.event_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.venuex.event_service.entities.Venue;

public interface VenueRepository extends JpaRepository<Venue, Integer> {
    boolean existsByName(String name);
}
