package com.venuex.transaction_service.controller;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.venuex.transaction_service.DTO.BookingDTO;
import com.venuex.transaction_service.DTO.TicketDTO;
import com.venuex.transaction_service.DTO.TicketReturnDTO;
import com.venuex.transaction_service.service.BookingService;
import com.venuex.transaction_service.service.TicketService;

@RestController
@RequestMapping("/api")
public class BookingController {

    private final BookingService bookingService;
    private final TicketService ticketService;

    public BookingController(BookingService bookingService, TicketService ticketService) {
        this.bookingService = bookingService;
        this.ticketService = ticketService;
    }

    // Create a new booking
    @PostMapping("/user/bookings")
    public ResponseEntity<Integer> createBooking(
            @RequestParam Integer eventId,
            HttpServletRequest request) {

        Integer userId = (Integer) request.getAttribute("userId");
        String userEmail = (String) request.getAttribute("userEmail"); // optional snapshot

        Integer bookingId = bookingService.createBooking(eventId, userId, userEmail);
        return ResponseEntity.ok(bookingId);
    }

    // Get all bookings for a user
    @GetMapping("/user/bookings")
    public ResponseEntity<List<BookingDTO>> getUserBookings(HttpServletRequest request) {

        Integer userId = (Integer) request.getAttribute("userId");
        return ResponseEntity.ok(bookingService.getUserBookings(userId));
    }

    // Add tickets to booking
    @PostMapping("/user/bookings/{bookingId}/tickets")
    public ResponseEntity<BookingDTO> addTicketsToBooking(
            @PathVariable Integer bookingId,
            @RequestBody List<TicketDTO> tickets,
            HttpServletRequest request) {

        Integer userId = (Integer) request.getAttribute("userId");
        BookingDTO bookingDTO = ticketService.addTicketsToBooking(bookingId, tickets, userId);
        return ResponseEntity.ok(bookingDTO);
    }

    // Get tickets for booking
    @GetMapping("/user/bookings/{bookingId}/tickets")
    public ResponseEntity<List<TicketReturnDTO>> getTicketsForBooking(
            @PathVariable Integer bookingId,
            HttpServletRequest request) {

        Integer userId = (Integer) request.getAttribute("userId");
        return ResponseEntity.ok(ticketService.getTicketsForBooking(bookingId, userId));
    }

    // Mock payment
    @PostMapping("/user/bookings/{bookingId}/payment")
    public ResponseEntity<String> mockPay(
            @PathVariable Integer bookingId,
            HttpServletRequest request) {

        Integer userId = (Integer) request.getAttribute("userId");
        return ResponseEntity.ok(ticketService.mockPay(bookingId, userId));
    }
}