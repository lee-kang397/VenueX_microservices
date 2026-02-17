package com.venuex.event_service.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.venuex.event_service.entities.SeatSection;
import com.venuex.event_service.entities.Venue;
import com.venuex.event_service.repository.SeatSectionRepository;
import com.venuex.event_service.repository.VenueRepository;

@Service
public class VenueService {
    private final VenueRepository venueRepository;
    private final SeatSectionRepository seatSectionRepository;

    public VenueService (VenueRepository venueRepository, SeatSectionRepository seatSectionRepository) {
        this.venueRepository = venueRepository;
        this.seatSectionRepository = seatSectionRepository;
    }

    //Venue get methods 
    public List<Venue> getAllVenues() {
        return venueRepository.findAll();
    }

    public Venue findById(Integer id) {
        return venueRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue not found"));
    }

    //create, update, delete only by ADMINS
    public Venue createVenue(Venue venue) {
        if (venueRepository.existsByName(venue.getName().toUpperCase())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Venue already exists");
        }
        if (venue.getName() != null) venue.setName(venue.getName().toUpperCase());
        if (venue.getLocation() != null) venue.setLocation(venue.getLocation().toLowerCase());
        if (venue.getDescription() != null) venue.setDescription(venue.getDescription().toLowerCase());
        return venueRepository.save(venue);
    }

    public Venue updateVenue(Integer id, Venue updatedVenue) {
        Venue existingVenue = venueRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue not found"));

        if (updatedVenue.getName() != null) {
            String newName = updatedVenue.getName().toUpperCase();

            if (!existingVenue.getName().equals(newName) && venueRepository.existsByName(newName)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Venue already exists");
            }
            existingVenue.setName(newName);
        }
        
        if (updatedVenue.getLocation() != null) {
            existingVenue.setLocation(updatedVenue.getLocation().toLowerCase());
        }
        if (updatedVenue.getDescription() != null) {
            existingVenue.setDescription(updatedVenue.getDescription().toLowerCase());
        }

        return venueRepository.save(existingVenue);
    }

    public void deleteVenue(Integer id) {
        Venue existingVenue = venueRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue not found"));
        venueRepository.delete(existingVenue);
    }

    //Seat Section methods 
    public List<SeatSection> getVenueSeatSections(Integer id) {
        venueRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue not found"));
        return seatSectionRepository.findByVenueId(id);
    }

    public List<SeatSection> createSeatSections(Integer venueId, List<SeatSection> sections) {
        Venue venue = venueRepository.findById(venueId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue not found"));

        for (SeatSection section : sections) {
            section.setVenue(venue);
        }
        return seatSectionRepository.saveAll(sections);
    }

    public List<SeatSection> updateSeatSections(Integer venueId, Map<String, Integer> capacities) {
         venueRepository.findById(venueId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue not found"));

        List<SeatSection> sections = seatSectionRepository.findByVenueId(venueId);
        if (sections.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No seat sections found for venue");
        }
        for (SeatSection section : sections) {
            Integer newCapacity = capacities.get(section.getType());
            if (newCapacity != null) {
                if (newCapacity <= 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Capacity must be greater than 0");
                }
                section.setCapacity(newCapacity);
            }
        }
        return seatSectionRepository.saveAll(sections);
    }
}