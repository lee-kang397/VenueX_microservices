package com.venuex.venue_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.venuex.venue_service.entities.SeatSection;

import java.util.List;
import java.util.Optional;
public interface SeatSectionRepository extends JpaRepository <SeatSection, Integer> {
    
    List<SeatSection> findByVenueId(Integer venueId);
    Optional<SeatSection> findByTypeAndVenue_Id(String type, Integer venueId);
}
