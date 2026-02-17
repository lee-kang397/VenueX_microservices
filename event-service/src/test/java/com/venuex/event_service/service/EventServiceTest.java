package com.venuex.event_service.service;

import com.venuex.event_service.dto.EventDTO;
import com.venuex.event_service.dto.EventSeatSectionDTO;
import com.venuex.event_service.dto.SeatSectionDTO;
import com.venuex.event_service.entities.Event;
import com.venuex.event_service.entities.EventSeatSection;
import com.venuex.event_service.feinInt.VenueClient;
import com.venuex.event_service.repository.EventRepository;
import com.venuex.event_service.repository.EventSeatSectionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventSeatSectionRepository eventSeatSectionRepository;

    @Mock
    private VenueClient venueClient;

    @InjectMocks
    private EventService eventService;

    private Event event;

    @BeforeEach
    void setup() {
        event = new Event();
        event.setId(1);
        event.setName("CONCERT");
        event.setVenueId(10);
        event.setCreatedByUserId(5);
        event.setStartTime(LocalDateTime.now().plusDays(2));
        event.setStatus("OPEN");
    }

    /* ========================= */
    /* EVENT TESTS */
    /* ========================= */

    @Test
    void getEventById_notFound() {
        when(eventRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class,
                () -> eventService.getEventById(1));
    }

    @Test
    void addEvent_conflictName() {
        when(eventRepository.existsByName("CONCERT")).thenReturn(true);

        assertThrows(ResponseStatusException.class,
                () -> eventService.addEvent(event, 5));
    }

    @Test
    void updateEvent_forbidden() {
        event.setCreatedByUserId(1);

        when(eventRepository.findById(1)).thenReturn(Optional.of(event));

        assertThrows(ResponseStatusException.class,
                () -> eventService.updateEvent(1, event, 99, "USER"));
    }

    @Test
    void deleteEvent_success() {
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));

        eventService.deleteEvent(1, 5, "USER");

        verify(eventRepository, times(1)).delete(event);
    }

    /* ========================= */
    /* SEAT SECTION TESTS */
    /* ========================= */

    @Test
    void getEventSeatById_success() {
        EventSeatSection ess = new EventSeatSection();
        ess.setSeatSectionId(100);
        ess.setPrice(BigDecimal.valueOf(50));
        ess.setRemainingCapacity(100);

        SeatSectionDTO seatDTO = new SeatSectionDTO();
        seatDTO.setId(100);
        seatDTO.setType("VIP");
        seatDTO.setCapacity(100);

        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        when(eventSeatSectionRepository.findByEvent_Id(1))
                .thenReturn(List.of(ess));
        when(venueClient.getSeatSectionsByVenue(10))
                .thenReturn(List.of(seatDTO));

        List<EventSeatSectionDTO> result =
                eventService.getEventSeatById(1);

        assertEquals(1, result.size());
        assertEquals("VIP", result.get(0).getSeatSectionName());
    }

    @Test
    void addEventSeatSectionPrices_conflict() {
        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        when(eventSeatSectionRepository.findByEvent_Id(1))
                .thenReturn(List.of(new EventSeatSection()));

        assertThrows(ResponseStatusException.class,
                () -> eventService.addEventSeatSectionPrices(1, new ArrayList<>()));
    }

    @Test
    void updateEventSeatSectionPrices_badPriceIncrease() {
        EventSeatSection ess = new EventSeatSection();
        ess.setSeatSectionId(100);
        ess.setPrice(BigDecimal.valueOf(50));
        ess.setRemainingCapacity(100);

        SeatSectionDTO seatDTO = new SeatSectionDTO();
        seatDTO.setId(100);
        seatDTO.setType("VIP");

        EventSeatSectionDTO updateDTO =
                new EventSeatSectionDTO("VIP",
                        BigDecimal.valueOf(100),
                        100);

        when(eventRepository.findById(1)).thenReturn(Optional.of(event));
        when(eventSeatSectionRepository.findByEvent_Id(1))
                .thenReturn(List.of(ess));
        when(venueClient.getSeatSectionsByVenue(10))
                .thenReturn(List.of(seatDTO));

        assertThrows(ResponseStatusException.class,
                () -> eventService.updateEventSeatSectionPrices(
                        1, List.of(updateDTO), 5, "USER"));
    }
}
