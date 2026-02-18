package com.venuex.transaction_service.feign;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.venuex.transaction_service.DTO.EventDTO;

@FeignClient(name = "event-service")
public interface EventClient {

    record EventSummary(Integer id, String name, String status) {
    }

    record EventReservationItem(String seatSectionType, int quantity) {
    }

    record EventReservationResult(List<ReservedItem> items) {
        public record ReservedItem(String seatSectionType, int quantity, BigDecimal unitPrice) {
        }
    }

    @GetMapping("/api/events/{eventId}/summary")
    EventSummary getSummary(@PathVariable Integer eventId);

    @PostMapping("/api/events/{eventId}/reservations")
    EventReservationResult reserveSeats(@PathVariable Integer eventId,
            @RequestBody List<EventReservationItem> items);

    @PostMapping("/api/events/{eventId}/reservations/{bookingId}/confirm")
    void confirmSeats(@PathVariable Integer eventId, @PathVariable Integer bookingId);
}
