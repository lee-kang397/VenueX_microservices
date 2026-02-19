package com.venuex.event_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.venuex.event_service.dto.EventDTO;
import com.venuex.event_service.dto.EventSeatSectionDTO;
import com.venuex.event_service.entities.Event;
import com.venuex.event_service.service.EventService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/events")
    public List<EventDTO> getEvents() {
        return eventService.getEvents();
    }

    @GetMapping("/events/{id}")
    public EventDTO getEventById(@PathVariable Integer id) {
        return eventService.getEventById(id);
    }

    @GetMapping("host/events")
    public List<EventDTO> getEventByCreator(HttpServletRequest request) {
        Integer userId = Integer.parseInt(request.getHeader("X-User-Id"));
        return eventService.getEventByCreator(userId);
    }

    @PostMapping("/host/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventDTO addEvent(@RequestBody Event event, HttpServletRequest request) {
        Integer userId = Integer.parseInt(request.getHeader("X-User-Id"));
        return eventService.addEvent(event, userId);
    }

    @PutMapping("/host/events/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventDTO updateEvent(@PathVariable Integer id, @RequestBody Event event, HttpServletRequest request) {
        Integer userId = Integer.parseInt(request.getHeader("X-User-Id"));
        String role = request.getHeader("X-User-Role");

        return eventService.updateEvent(id, event, userId, role);
    }

    @DeleteMapping("/host/events/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable Integer id, HttpServletRequest request) {
        Integer userId = Integer.parseInt(request.getHeader("X-User-Id"));
        String role = request.getHeader("X-User-Role");

        eventService.deleteEvent(id, userId, role);
    }

    /* ============================= */
    /* EVENT SEAT SECTIONS */
    /* ============================= */

    @GetMapping("/events/{id}/event-seat-section")
    public List<EventSeatSectionDTO> getEventSeatById(@PathVariable Integer id) {
        return eventService.getEventSeatById(id);
    }

    @PostMapping("/host/events/{id}/event-seat-section")
    @ResponseStatus(HttpStatus.CREATED)
    public void addEventSeatSectionPrices(
            @PathVariable Integer id,
            @RequestBody List<EventSeatSectionDTO> seatSections) {
        eventService.addEventSeatSectionPrices(id, seatSections);
    }

    @PutMapping("/host/events/{id}/event-seat-section")
    @ResponseStatus(HttpStatus.OK)
    public List<EventSeatSectionDTO> updateEventSeatSectionPrices(
            @PathVariable Integer id,
            @RequestBody List<EventSeatSectionDTO> seatSections,
            HttpServletRequest request) {

        Integer userId = Integer.parseInt(request.getHeader("X-User-Id"));
        String role = request.getHeader("X-User-Role");

        return eventService.updateEventSeatSectionPrices(id, seatSections, userId, role);
    }
}
