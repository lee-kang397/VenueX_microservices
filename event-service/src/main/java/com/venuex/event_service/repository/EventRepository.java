package com.venuex.event_service.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.venuex.event_service.entities.Event;

public interface EventRepository extends JpaRepository<Event, Integer>{

    @Query("""
        SELECT COUNT(e) > 0
        FROM Event e
        WHERE e.venueId = :venueId
            AND e.startTime >= :dayStart
            AND e.startTime < :dayEnd
    """)
    boolean existsEventOnDay(Integer venueId, LocalDateTime dayStart, LocalDateTime dayEnd);

    @Query("""
        SELECT COUNT(e) > 0
        FROM Event e
        WHERE e.venueId = :venueId
            AND e.startTime >= :dayStart
            AND e.startTime < :dayEnd
            AND e.id <> :eventId
    """)
    boolean existsEventOnDayExcludingEvent(
        Integer venueId,
        LocalDateTime dayStart,
        LocalDateTime dayEnd,
        Integer eventId);

    @Modifying
    @Query("DELETE FROM Event e WHERE e.startTime <= :cutoff")
    void deleteExpiredEvents(LocalDateTime cutoff);

    boolean existsByName(String name);

    List<Event> findByCreatedByUserId(Integer UserId);
}
