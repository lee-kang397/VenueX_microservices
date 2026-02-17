package com.venuex.event_service.service;

import com.venuex.event_service.entities.SeatSection;
import com.venuex.event_service.entities.Venue;
import com.venuex.event_service.repository.SeatSectionRepository;
import com.venuex.event_service.repository.VenueRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VenueServiceTest {

    @Mock
    private VenueRepository venueRepository;

    @Mock
    private SeatSectionRepository seatSectionRepository;

    @InjectMocks
    private VenueService venueService;

    private Venue venue;

    @BeforeEach
    void setUp() {
        venue = new Venue();
        venue.setId(1);
        venue.setName("TEST");
        venue.setLocation("arlington");
        venue.setDescription("desc");
    }

    /* ============================= */
    /* VENUE TESTS */
    /* ============================= */

    @Test
    void getAllVenues_success() {
        when(venueRepository.findAll()).thenReturn(List.of(venue));

        List<Venue> result = venueService.getAllVenues();

        assertEquals(1, result.size());
    }

    @Test
    void findById_success() {
        when(venueRepository.findById(1)).thenReturn(Optional.of(venue));

        Venue result = venueService.findById(1);

        assertEquals("TEST", result.getName());
    }

    @Test
    void findById_notFound() {
        when(venueRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> venueService.findById(1));
    }

    @Test
    void createVenue_success() {
        when(venueRepository.existsByName("TEST")).thenReturn(false);
        when(venueRepository.save(any())).thenReturn(venue);

        Venue result = venueService.createVenue(venue);

        assertEquals("TEST", result.getName());
    }

    @Test
    void createVenue_conflict() {
        when(venueRepository.existsByName("TEST")).thenReturn(true);

        assertThrows(ResponseStatusException.class,
                () -> venueService.createVenue(venue));
    }

    @Test
    void updateVenue_success() {
        when(venueRepository.findById(1)).thenReturn(Optional.of(venue));
        when(venueRepository.save(any())).thenReturn(venue);

        Venue updated = new Venue();
        updated.setName("NEWNAME");

        Venue result = venueService.updateVenue(1, updated);

        assertEquals("NEWNAME", result.getName());
    }

    @Test
    void deleteVenue_success() {
        when(venueRepository.findById(1)).thenReturn(Optional.of(venue));

        venueService.deleteVenue(1);

        verify(venueRepository, times(1)).delete(venue);
    }

    /* ============================= */
    /* SEAT SECTION TESTS */
    /* ============================= */

    @Test
    void getVenueSeatSections_success() {
        when(venueRepository.findById(1)).thenReturn(Optional.of(venue));
        when(seatSectionRepository.findByVenueId(1))
                .thenReturn(List.of(new SeatSection()));

        List<SeatSection> result = venueService.getVenueSeatSections(1);

        assertEquals(1, result.size());
    }

    @Test
    void updateSeatSections_badCapacity() {
        when(venueRepository.findById(1)).thenReturn(Optional.of(venue));

        SeatSection section = new SeatSection();
        section.setType("VIP");
        section.setCapacity(100);

        when(seatSectionRepository.findByVenueId(1))
                .thenReturn(List.of(section));

        Map<String, Integer> capacities = Map.of("VIP", 0);

        assertThrows(ResponseStatusException.class,
                () -> venueService.updateSeatSections(1, capacities));
    }
}
