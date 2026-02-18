package com.venuex.transaction_service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.venuex.transaction_service.DTO.BookingDTO;
import com.venuex.transaction_service.entities.Booking;
import com.venuex.transaction_service.entities.Ticket;
import com.venuex.transaction_service.feign.EventClient;
import com.venuex.transaction_service.feign.UserClient;
import com.venuex.transaction_service.repository.BookingRepository;

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

        /**
         * Called by BookingController:
         * createBooking(eventId, userId, userEmail)
         */
        public Integer createBooking(Integer eventId, Integer userId, String userEmail) {
                validateId(eventId, "eventId");
                validateId(userId, "userId");

                // Validate event exists / is bookable
                var eventSummary = eventClient.getSummary(eventId);
                if (eventSummary == null) {
                        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found");
                }
                // Your EventClient.EventSummary currently has: (id, name, status)
                // You were previously checking "CLOSED". Keep that same contract.
                if (eventSummary.status() != null && "CLOSED".equalsIgnoreCase(eventSummary.status())) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event is closed / sold out");
                }

                // Fill email snapshot if missing
                String emailSnapshot = (userEmail == null || userEmail.isBlank())
                                ? safeResolveEmail(userId)
                                : userEmail;

                Booking booking = new Booking();
                booking.setUserId(userId);
                booking.setUserEmail(emailSnapshot);

                booking.setEventId(eventSummary.id());
                booking.setEventName(eventSummary.name());

                booking.setStatus(Booking.BookingStatus.PENDING);
                booking.setBookedAt(LocalDateTime.now());

                bookingRepository.save(booking);
                return booking.getId();
        }

        /**
         * Called by BookingController:
         * getUserBookings(userId)
         */
        public List<BookingDTO> getUserBookings(Integer userId) {
                validateId(userId, "userId");

                List<Booking> bookings = bookingRepository.findByUserIdOrderByBookedAtDesc(userId);

                return bookings.stream().map(b -> {
                        BigDecimal total = (b.getTickets() == null)
                                        ? BigDecimal.ZERO
                                        : b.getTickets().stream()
                                                        .map(Ticket::getPrice)
                                                        .filter(p -> p != null)
                                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                        return new BookingDTO(
                                        b.getId(),
                                        b.getUserEmail(),
                                        b.getEventName(),
                                        b.getBookedAt(),
                                        total);
                }).toList();
        }

        /**
         * Handy for TicketService / PaymentService:
         * Ensures the booking exists AND belongs to the current user.
         */
        public Booking getOwnedBookingOrThrow(Integer bookingId, Integer userId) {
                validateId(bookingId, "bookingId");
                validateId(userId, "userId");

                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Booking not found"));

                if (!userId.equals(booking.getUserId())) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized");
                }
                return booking;
        }

        private void validateId(Integer id, String field) {
                if (id == null || id <= 0) {
                        throw new IllegalArgumentException("Invalid " + field);
                }
        }

        private String safeResolveEmail(Integer userId) {
                try {
                        var user = userClient.getUserById(userId);
                        return (user == null) ? null : user.email();
                } catch (Exception ex) {
                        return null;
                }
        }
}
