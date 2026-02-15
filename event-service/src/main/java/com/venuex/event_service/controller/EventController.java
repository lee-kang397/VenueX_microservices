package com.venuex.event_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.venuex.event_service.dto.EventDTO;
import com.venuex.event_service.entities.Event;
import com.venuex.event_service.entities.EventSeatSection;
import com.venuex.event_service.service.EventService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<EventDTO> getEvents() {
        return eventService.getEvents();
    }

    @GetMapping("/{id}")
    public EventDTO getEventById(@PathVariable Integer id) {
        return eventService.getEventById(id);
    }

    @GetMapping("/host")
    public List<EventDTO> getEventByCreator(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        return eventService.getEventByCreator(userId);
    }

    @PostMapping("/host")
    @ResponseStatus(HttpStatus.CREATED)
    public EventDTO addEvent(@RequestBody Event event,HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        return eventService.addEvent(event, userId);
    }

    @PutMapping("/host/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventDTO updateEvent(@PathVariable Integer id, @RequestBody Event event, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        String role = (String) request.getAttribute("userRole");

        return eventService.updateEvent(id, event, userId, role);
    }

    @DeleteMapping("/host/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable Integer id, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        String role = (String) request.getAttribute("userRole");

        eventService.deleteEvent(id, userId, role);
    }

    /* ============================= */
    /* EVENT SEAT SECTIONS */
    /* ============================= */

    @GetMapping("/{id}/seat-sections")
    public List<EventSeatSection> getEventSeatById(@PathVariable Integer id) {
        return eventService.getEventSeatById(id);
    }

    @PostMapping("/host/{id}/seat-sections")
    @ResponseStatus(HttpStatus.CREATED)
    public void addEventSeatSectionPrices(
            @PathVariable Integer id,
            @RequestBody List<EventSeatSection> seatSections) {
        eventService.addEventSeatSectionPrices(id, seatSections);
    }

    @PutMapping("/host/{id}/seat-sections")
    @ResponseStatus(HttpStatus.OK)
    public List<EventSeatSection> updateEventSeatSectionPrices(
            @PathVariable Integer id,
            @RequestBody List<EventSeatSection> seatSections,
            HttpServletRequest request) {

        Integer userId = (Integer) request.getAttribute("userId");
        String role = (String) request.getAttribute("userRole");

        return eventService.updateEventSeatSectionPrices(id, seatSections, userId, role);
    }
}
