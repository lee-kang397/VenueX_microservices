package com.venuex.transaction_service.service;

import com.venuex.transaction_service.DTO.BookingDTO;
import com.venuex.transaction_service.entities.Booking;
import com.venuex.transaction_service.entities.Ticket;
import com.venuex.transaction_service.repository.BookingRepository;
import com.venuex.transaction_service.service.client.EventClient;
import com.venuex.transaction_service.service.client.UserClient;
import com.venuex.transaction_service.service.client.dto.EventSummary;
import com.venuex.transaction_service.service.client.dto.UserSummary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingService {

        private final BookingRepository bookingRepository;
        private final UserClient userClient;
        private final EventClient eventClient;

        public BookingService(
                        BookingRepository bookingRepository,
                        UserClient userClient,
                        EventClient eventClient) {
                this.bookingRepository = bookingRepository;
                this.userClient = userClient;
                this.eventClient = eventClient;
        }

        @Transactional
        public Integer createBooking(Integer eventId, String userEmail) {

                // 1) Resolve user via user-service (or stub)
                UserSummary user = userClient.getUserByEmail(userEmail)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

                // 2) Resolve event via event-service (or stub)
                EventSummary event = eventClient.getEventSummary(eventId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Event not found"));

                // Example business rule check (your event-service should own "sold out" truth)
                if (event.isSoldOut()) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Event seats sold out");
                }

                // 3) Create booking locally (no external entity relationships)
                Booking booking = new Booking();
                booking.setUserId(user.id());
                booking.setEventId(event.id());

                // Optional snapshots (only if your Booking entity/table has these fields)
                booking.setUserEmail(user.email());
                booking.setEventName(event.name());
                booking.setEventStartTime(event.startTime());

                booking.setStatus(Booking.BookingStatus.PENDING);
                booking.setBookedAt(LocalDateTime.now());

                booking.addTicket(new Ticket("GA", new BigDecimal("50.00")));

                bookingRepository.save(booking);

                return booking.getId();
        }

        @Transactional(readOnly = true)
        public List<BookingDTO> getUserBookings(String userEmail) {

                UserSummary user = userClient.getUserByEmail(userEmail)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

                List<Booking> bookings = bookingRepository.findByUserId(user.id());

                return bookings.stream()
                                .filter(b -> b.getStatus() == Booking.BookingStatus.BOOKED)
                                .map(booking -> {
                                        BigDecimal total = booking.getTickets().stream()
                                                        .map(Ticket::getPrice)
                                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                                        // Use snapshots if available; fall back to IDs if not
                                        String eventName = booking.getEventName() != null ? booking.getEventName()
                                                        : ("event#" + booking.getEventId());
                                        String email = booking.getUserEmail() != null ? booking.getUserEmail()
                                                        : user.email();

                                        return new BookingDTO(
                                                        booking.getId(),
                                                        email,
                                                        eventName,
                                                        booking.getBookedAt(),
                                                        total);
                                })
                                .collect(Collectors.toList());
        }
}
