package com.venuex.event_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.venuex.event_service.entities.SeatSection;

import java.util.List;
import java.util.Optional;
public interface SeatSectionRepository extends JpaRepository <SeatSection, Integer> {
    
    List<SeatSection> findByVenueId(Integer venueId);
    Optional<SeatSection> findByTypeAndVenue_Id(String type, Integer venueId);
}
