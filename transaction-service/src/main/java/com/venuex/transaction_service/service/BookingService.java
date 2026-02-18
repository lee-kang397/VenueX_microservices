package com.venuex.transaction_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.venuex.transaction_service.DTO.BookingDTO;
import com.venuex.transaction_service.entities.Booking;
import com.venuex.transaction_service.entities.Booking.BookingStatus;
import com.venuex.transaction_service.feign.EventClient;
import com.venuex.transaction_service.repository.BookingRepository;

@Service
public class BookingService {

        private final BookingRepository bookingRepository;
        private final EventClient eventClient;

        public BookingService(BookingRepository bookingRepository, EventClient eventClient) {
                this.bookingRepository = bookingRepository;
                this.eventClient = eventClient;
        }

        public Integer createBooking(Integer eventId, Integer userId, String userEmailSnapshot) {
                if (eventId == null || eventId <= 0) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid eventId");
                }
                if (userId == null || userId <= 0) {
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user identity");
                }

                // Validate event exists + not closed
                EventClient.EventSummary eventSummary;
                try {
                        eventSummary = eventClient.getSummary(eventId);
                } catch (Exception ex) {
                        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Event service unavailable");
                }
                if (eventSummary == null) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found");
                }
                if ("CLOSED".equalsIgnoreCase(eventSummary.status())) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Event is sold out");
                }

                Booking booking = new Booking();
                booking.setUserId(userId);
                booking.setEventId(eventId);
                booking.setStatus(BookingStatus.PENDING);
                booking.setBookedAt(LocalDateTime.now());

                // snapshots (optional)
                booking.setUserEmail(userEmailSnapshot); // ok to store email snapshot, but don't rely on it for auth
                booking.setEventName(eventSummary.name());
                booking.setEventStartTime(null); // add to EventSummary if you want it

                bookingRepository.save(booking);
                return booking.getId();
        }

        public List<BookingDTO> getUserBookings(Integer userId) {
                if (userId == null || userId <= 0) {
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user identity");
                }

                return bookingRepository.findByUserIdOrderByBookedAtDesc(userId)
                                .stream()
                                .map(b -> new BookingDTO(
                                                b.getId(),
                                                b.getUserEmail(), // snapshot
                                                b.getEventName(), // snapshot
                                                b.getBookedAt(),
                                                null // total: compute in TicketService or store on booking
                                ))
                                .collect(Collectors.toList());
        }
}