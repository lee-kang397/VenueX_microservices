package com.venuex.transaction_service.controller;

import com.venuex.transaction_service.DTO.BookingDTO;
import com.venuex.transaction_service.DTO.TicketDTO;
import com.venuex.transaction_service.DTO.TicketReturnDTO;
import com.venuex.transaction_service.service.BookingService;
import com.venuex.transaction_service.service.TicketService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class BookingController {

    private final BookingService bookingService;
    private final TicketService ticketService;

    public BookingController(BookingService bookingService, TicketService ticketService) {
        this.bookingService = bookingService;
        this.ticketService = ticketService;
    }

    /**
     * Helper: fetch the authenticated user's email injected by your JWT filter.
     * If you later move to Spring Security, you can replace this with
     * Authentication/Principal.
     */
    private String requireUserEmail(HttpServletRequest request) {
        Object val = request.getAttribute("userEmail");
        if (val == null || ((String) val).isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing user identity");
        }
        return (String) val;
    }

    // Create a new booking (creates local booking record, usually PENDING)
    @PostMapping("/bookings")
    public ResponseEntity<Integer> createBooking(
            @RequestParam Integer eventId,
            HttpServletRequest request) {
        String userEmail = requireUserEmail(request);
        Integer bookingId = bookingService.createBooking(eventId, userEmail);

        // Nice REST touch: Location header for the created resource
        return ResponseEntity
                .created(URI.create("/api/transactions/bookings/" + bookingId))
                .body(bookingId);
    }

    // Get all bookings for the authenticated user
    @GetMapping("/bookings")
    public ResponseEntity<List<BookingDTO>> getUserBookings(HttpServletRequest request) {
        String userEmail = requireUserEmail(request);
        return ResponseEntity.ok(bookingService.getUserBookings(userEmail));
    }

    // Add tickets to an existing booking (via event-service reservation)
    @PostMapping("/bookings/{bookingId}/tickets")
    public ResponseEntity<BookingDTO> addTicketsToBooking(
            @PathVariable Integer bookingId,
            @RequestBody List<TicketDTO> tickets,
            HttpServletRequest request) {
        String userEmail = requireUserEmail(request);
        BookingDTO bookingDTO = ticketService.addTicketsToBooking(bookingId, tickets, userEmail);
        return ResponseEntity.ok(bookingDTO);
    }

    // Get tickets for a booking (must belong to authenticated user)
    @GetMapping("/bookings/{bookingId}/tickets")
    public ResponseEntity<List<TicketReturnDTO>> getTicketsForBooking(
            @PathVariable Integer bookingId,
            HttpServletRequest request) {
        String userEmail = requireUserEmail(request);
        return ResponseEntity.ok(ticketService.getTicketsForBooking(bookingId, userEmail));
    }

    // Mock payment for a booking
    @PostMapping("/bookings/{bookingId}/payment")
    public ResponseEntity<String> mockPay(
            @PathVariable Integer bookingId,
            HttpServletRequest request) {
        String userEmail = requireUserEmail(request);
        return ResponseEntity.ok(ticketService.mockPay(bookingId, userEmail));
    }
}