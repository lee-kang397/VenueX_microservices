package com.venuex.event_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import com.venuex.event_service.entities.SeatSection;
import com.venuex.event_service.entities.Venue;
import com.venuex.event_service.service.VenueService;;

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
    public Venue addVenue(@RequestBody Venue venue) {
        return venueService.createVenue(venue);
    }

    @PutMapping("/admin/venues/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Venue updateVenue(@PathVariable Integer id, @RequestBody Venue venue) {
        return venueService.updateVenue(id, venue);
    }

    @DeleteMapping("/admin/venues/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVenue(@PathVariable Integer id) {
        venueService.deleteVenue(id);
    }

    //Seat Section Operations 
     @GetMapping("/venues/{venueId}/seat-sections")
    public List<SeatSection> getVenueSeatSections(@PathVariable Integer venueId) {
        return venueService.getVenueSeatSections(venueId);
    }

    @PostMapping("/admin/venues/{venueId}/seat-sections")
    @ResponseStatus(HttpStatus.CREATED)
    public List<SeatSection> createSeatSections(@PathVariable Integer venueId, @RequestBody List<SeatSection> sections) {
        return venueService.createSeatSections(venueId, sections);
    }

    @PutMapping("/admin/venues/{venueId}/seat-sections")
    @ResponseStatus(HttpStatus.OK)
    public List<SeatSection> updateSeatSections(@PathVariable Integer venueId, @RequestBody Map<String, Integer> sections) {
        return venueService.updateSeatSections(venueId, sections);
    }
}