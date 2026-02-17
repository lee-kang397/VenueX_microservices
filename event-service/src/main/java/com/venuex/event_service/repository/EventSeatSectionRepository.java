package com.venuex.event_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.venuex.event_service.entities.EventSeatSection;

@Repository
public interface EventSeatSectionRepository extends JpaRepository<EventSeatSection, Integer> {

    List<EventSeatSection> findByEvent_Id(Integer eventId);

    Optional<EventSeatSection> findByEvent_IdAndSeatSectionId(
        Integer eventId,
        Integer seatSectionId);
}

