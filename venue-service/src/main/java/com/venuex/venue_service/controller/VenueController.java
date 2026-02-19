package com.venuex.venue_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.venuex.venue_service.entities.SeatSection;
import com.venuex.venue_service.entities.Venue;
import com.venuex.venue_service.service.VenueService;

import org.springframework.http.HttpStatus;

import jakarta.servlet.http.HttpServletRequest;;

@RestController
@RequestMapping("/api")
public class VenueController {
    private final VenueService venueService;

    public VenueController (VenueService venueService) {
        this.venueService = venueService;
    }
    
    @GetMapping("/venues")
    public List<Venue> getAllVenues() {
        return venueService.getAllVenues();
    }

    @GetMapping("/venues/{id}")
    public Venue getVenueById(@PathVariable Integer id) {
        return venueService.findById(id);
    }

    //ADMINS only 
    @PostMapping("/admin/venues")
    @ResponseStatus(HttpStatus.CREATED)
    public Venue addVenue(@RequestBody Venue venue, HttpServletRequest request) {
        String requesterRole = request.getHeader("X-User-Role");
        if (!requesterRole.equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return venueService.createVenue(venue);
    }

    @PutMapping("/admin/venues/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Venue updateVenue(@PathVariable Integer id, @RequestBody Venue venue, HttpServletRequest request) {
        String requesterRole = request.getHeader("X-User-Role");
        if (!requesterRole.equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return venueService.updateVenue(id, venue);
    }

    @DeleteMapping("/admin/venues/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVenue(@PathVariable Integer id, HttpServletRequest request) {
        String requesterRole = request.getHeader("X-User-Role");
        if (!requesterRole.equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        venueService.deleteVenue(id);
    }

    //Seat Section Operations 
     @GetMapping("/venues/{venueId}/seat-sections")
    public List<SeatSection> getVenueSeatSections(@PathVariable Integer venueId, HttpServletRequest request) {
        String requesterRole = request.getHeader("X-User-Role");
        if (!requesterRole.equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return venueService.getVenueSeatSections(venueId);
    }

    @PostMapping("/admin/venues/{venueId}/seat-sections")
    @ResponseStatus(HttpStatus.CREATED)
    public List<SeatSection> createSeatSections(@PathVariable Integer venueId, @RequestBody List<SeatSection> sections, HttpServletRequest request) {
        String requesterRole = request.getHeader("X-User-Role");
        if (!requesterRole.equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return venueService.createSeatSections(venueId, sections);
    }

    @PutMapping("/admin/venues/{venueId}/seat-sections")
    @ResponseStatus(HttpStatus.OK)
    public List<SeatSection> updateSeatSections(@PathVariable Integer venueId, @RequestBody Map<String, Integer> sections, HttpServletRequest request) {
        String requesterRole = request.getHeader("X-User-Role");
        if (!requesterRole.equals("ADMIN")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return venueService.updateSeatSections(venueId, sections);
    }
}