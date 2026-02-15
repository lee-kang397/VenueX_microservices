package com.venuex.event_service.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.venuex.event_service.dto.EventDTO;
import com.venuex.event_service.entities.Event;
import com.venuex.event_service.entities.EventSeatSection;
import com.venuex.event_service.repository.EventRepository;
import com.venuex.event_service.repository.EventSeatSectionRepository;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final EventSeatSectionRepository eventSeatSectionRepository;

    public EventService(
        EventRepository eventRepository,
        EventSeatSectionRepository eventSeatSectionRepository) {
            this.eventRepository = eventRepository;
            this.eventSeatSectionRepository = eventSeatSectionRepository;
    }

    public List<EventDTO> getEvents() {
        List<Event> events = eventRepository.findAll();
    
        return events.stream()
            .peek(event -> event.setStatus(eventStatus(event.getId())))
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public EventDTO getEventById (Integer id) {
        Event event = eventRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        event.setStatus(eventStatus(id));
        return convertToDTO(event);
    }

    public List<EventDTO> getEventByCreator(Integer userId) {

        List<Event> events = eventRepository.findByCreatedByUserId(userId);
        return events.stream()
            .peek(event -> event.setStatus(eventStatus(event.getId())))
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public EventDTO addEvent(Event event, Integer userId) {

        event.setCreatedByUserId(userId);
        if(eventRepository.existsByName(event.getName().toUpperCase())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Event already exists");
        }

        validateEventStartTime(event.getStartTime());

        LocalDate eventDate = event.getStartTime().toLocalDate();
        LocalDateTime dayStart = eventDate.atStartOfDay();
        LocalDateTime dayEnd = eventDate.plusDays(1).atStartOfDay();
        boolean exists = eventRepository.existsEventOnDay(event.getVenueId(), dayStart, dayEnd);

        if (exists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "This venue already has a concert scheduled for that day");
        }

        if (event.getName() != null)
            event.setName(event.getName().toUpperCase());

        if (event.getDescription() != null)
            event.setDescription(event.getDescription().toLowerCase());

        Event saved = eventRepository.save(event);
        eventStatus(saved.getId());

        return convertToDTO(saved);
    }


    public EventDTO updateEvent(Integer id, Event event, Integer userId, String role) {

        Event existingEvent = eventRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        if (!existingEvent.getCreatedByUserId().equals(userId) && !role.equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not correct user");
        }

        // Name
        if (event.getName() != null) {
            String newName = event.getName().toUpperCase();

            if (!existingEvent.getName().equals(newName) &&
                eventRepository.existsByName(newName)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Event already exists");
            }
            existingEvent.setName(newName);
        }

    // Start Time
    if (event.getStartTime() != null) {

        validateEventStartTime(event.getStartTime());

        LocalDate eventDate = event.getStartTime().toLocalDate();
        LocalDateTime dayStart = eventDate.atStartOfDay();
        LocalDateTime dayEnd = eventDate.plusDays(1).atStartOfDay();

        boolean conflict = eventRepository.existsEventOnDayExcludingEvent(
            existingEvent.getVenueId(),
            dayStart,
            dayEnd,
            id
        );

        if (conflict) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "This venue already has a concert scheduled for that day"
            );
        }

        existingEvent.setStartTime(event.getStartTime());
    }

    if (event.getDescription() != null) {
        existingEvent.setDescription(event.getDescription().toLowerCase());
    }

    Event updatedEvent = eventRepository.save(existingEvent);
    updatedEvent.setStatus(eventStatus(id));

    return convertToDTO(updatedEvent);
}


    public void deleteEvent(Integer id, Integer userId, String role) {
        Event existingEvent = eventRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
    
        if (!existingEvent.getCreatedByUserId().equals(userId) && !role.equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not correct user");
        }
        eventRepository.delete(existingEvent);
    }

    /*================================================================================================= */
    /* Event Seat Sections */

    public List<EventSeatSection> getEventSeatById(Integer id) {
        eventRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        return eventSeatSectionRepository.findByEvent_Id(id);
    }


    public void addEventSeatSectionPrices(Integer eventId,
        List<EventSeatSection> seatSections, Integer userId, String role) {

        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        if (!event.getCreatedByUserId().equals(userId) && !role.equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not correct user");
        }

        List<EventSeatSection> existing = eventSeatSectionRepository.findByEvent_Id(eventId);

        if (!existing.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Event seat sections already set");
        }

        List<EventSeatSection> toSave = new ArrayList<>();

        for (EventSeatSection dto : seatSections) {
            EventSeatSection ess = new EventSeatSection();
            ess.setEvent(event);
            ess.setSeatSectionId(dto.getSeatSectionId()); 
            ess.setPrice(dto.getPrice());
            ess.setRemainingCapacity(dto.getRemainingCapacity()); 
            toSave.add(ess);
        }
        eventSeatSectionRepository.saveAll(toSave);
    }


    public List<EventSeatSection> updateEventSeatSectionPrices(Integer eventId,
        List<EventSeatSection> eventSeatSections, Integer userId,String role) {
        Event event = eventRepository.findById(eventId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));

        if (!event.getCreatedByUserId().equals(userId) && !role.equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not correct user");
        }

        List<EventSeatSection> existingSections = eventSeatSectionRepository.findByEvent_Id(eventId);
        if (existingSections.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No seat sections exist for this event");
        }

        for (EventSeatSection dto : eventSeatSections) {
            EventSeatSection existingSection = existingSections.stream()
                .filter(ess -> ess.getSeatSectionId()
                .equals(dto.getSeatSectionId()))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Seat section not found"));
            if (dto.getPrice().compareTo(existingSection.getPrice()) > 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Cannot increase price");
            }
            existingSection.setPrice(dto.getPrice());
        }

        eventSeatSectionRepository.saveAll(existingSections);
        return existingSections;
    }

     /*================================================================================================= */
    /* Helpers */
    private EventDTO convertToDTO(Event event) {
        return new EventDTO(
            event.getId(),
            event.getVenueId(),
            event.getName(),
            event.getDescription(),
            event.getStartTime(),
            event.getStatus());
    }

    public String eventStatus(Integer eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
        LocalDateTime now = LocalDateTime.now();
        boolean after2Hours = now.isAfter(event.getStartTime().plusHours(2));
        boolean isSoldOut = event.getSeatSections().stream()
            .allMatch(section -> section.getRemainingCapacity() == 0);

        String newStatus = (after2Hours || isSoldOut) ? "CLOSED" : "OPEN";

        if (!newStatus.equals(event.getStatus())) {
            event.setStatus(newStatus);
            eventRepository.save(event);
        }
        return newStatus;
    }

    private void validateEventStartTime(LocalDateTime startTime) {
        if (startTime == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Start time is required");
        }

        LocalDateTime now = LocalDateTime.now();

        // Must be at least 1 day after today
        if (!startTime.isAfter(now.plusDays(1))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Event must be scheduled at least 1 day in advance");
        }

        LocalTime time = startTime.toLocalTime();

        // Allowed time window: 8:00 AM – 10:00 PM
        LocalTime earliest = LocalTime.of(8, 0);
        LocalTime latest = LocalTime.of(22, 0);

        if (time.isBefore(earliest) || time.isAfter(latest)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Event start time must be between 8:00 AM and 10:00 PM");
        }
    }
}