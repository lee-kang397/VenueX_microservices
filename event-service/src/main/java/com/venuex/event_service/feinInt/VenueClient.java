package com.venuex.event_service.feinInt;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.venuex.event_service.dto.SeatSectionDTO;
import com.venuex.event_service.dto.VenueDTO;

@FeignClient(name = "venue-service")
public interface VenueClient {

    @GetMapping("/api/venues/{id}")
    VenueDTO getVenueById(@PathVariable("id") Integer id);

    @GetMapping("/venues/{venueId}/seat-sections")
    List<SeatSectionDTO> getSeatSectionsByVenue(@PathVariable Integer venueId);
}
