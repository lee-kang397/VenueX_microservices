package com.venuex.transaction_service.service;

import com.venuex.transaction_service.DTO.BookingDTO;
import com.venuex.transaction_service.DTO.TicketDTO;
import com.venuex.transaction_service.DTO.TicketReturnDTO;
import com.venuex.transaction_service.entities.Booking;
import com.venuex.transaction_service.entities.Payment;
import com.venuex.transaction_service.entities.Ticket;
import com.venuex.transaction_service.repository.BookingRepository;
import com.venuex.transaction_service.repository.PaymentRepository;
import com.venuex.transaction_service.repository.TicketRepository;
import com.venuex.transaction_service.service.client.EventClient;
import com.venuex.transaction_service.service.client.UserClient;
import com.venuex.transaction_service.service.client.dto.EventReservationItem;
import com.venuex.transaction_service.service.client.dto.EventReservationResult;
import com.venuex.transaction_service.service.client.dto.UserSummary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;

    private final UserClient userClient;
    private final EventClient eventClient;

    public TicketService(
            TicketRepository ticketRepository,
            BookingRepository bookingRepository,
            PaymentRepository paymentRepository,
            NotificationService notificationService,
            UserClient userClient,
            EventClient eventClient) {
        this.ticketRepository = ticketRepository;
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.notificationService = notificationService;
        this.userClient = userClient;
        this.eventClient = eventClient;
    }

    /**
     * Get tickets for a booking (authorization by user ownership).
     * Microservice: no joins to user/event tables.
     */
    @Transactional(readOnly = true)
    public List<TicketReturnDTO> getTicketsForBooking(Integer bookingId, String userEmail) {
        validateId(bookingId);

        UserSummary user = userClient.getUserByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        if (!user.id().equals(booking.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized user");
        }

        return ticketRepository.findByBookingId(bookingId)
                .stream()
                .map(ticket -> new TicketReturnDTO(
                        ticket.getId(),
                        ticket.getSeatSectionType(),
                        ticket.getPrice()))
                .collect(Collectors.toList());
    }

    /**
     * Add tickets to a booking by reserving seats through event-service.
     * This replaces local EventSeatSectionRepository / EventRepository logic.
     */
    @Transactional
    public BookingDTO addTicketsToBooking(Integer bookingId, List<TicketDTO> tickets, String userEmail) {
        validateId(bookingId);

        UserSummary user = userClient.getUserByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        if (!user.id().equals(booking.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized user");
        }

        if (booking.getStatus() == Booking.BookingStatus.CANCELED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Booking is canceled");
        }
        if (booking.getStatus() == Booking.BookingStatus.BOOKED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Booking is already confirmed");
        }

        if (tickets == null || tickets.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tickets request cannot be empty");
        }

        // 1) Reserve seats in event-service (single call preferred)
        List<EventReservationItem> reservationItems = tickets.stream()
                .map(t -> new EventReservationItem(t.getSeatSectionName(), t.getQuantity()))
                .collect(Collectors.toList());

        EventReservationResult reservation = eventClient.reserveSeats(booking.getEventId(), reservationItems)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Unable to reserve seats"));

        // 2) Create local tickets as HELD using returned pricing (snapshot)
        BigDecimal total = BigDecimal.ZERO;

        for (var reservedItem : reservation.items()) {
            String sectionType = reservedItem.seatSectionType();
            BigDecimal unitPrice = reservedItem.unitPrice();

            for (int i = 0; i < reservedItem.quantity(); i++) {
                Ticket ticket = new Ticket(sectionType, unitPrice);
                ticket.setStatus(Ticket.TicketStatus.HELD);
                booking.addTicket(ticket);
                total = total.add(unitPrice);
            }
        }

        // Keep booking PENDING until payment succeeds
        booking.setStatus(Booking.BookingStatus.PENDING);
        bookingRepository.save(booking);

        // Your BookingDTO can still return snapshots if you have them
        String eventName = booking.getEventName() != null ? booking.getEventName() : ("event#" + booking.getEventId());
        String email = booking.getUserEmail() != null ? booking.getUserEmail() : user.email();

        return new BookingDTO(
                booking.getId(),
                email,
                eventName,
                booking.getBookedAt(),
                total);
    }

    /**
     * Mock payment:
     * - checks ownership
     * - prevents double-pay
     * - computes total from local tickets
     * - marks payment PAID + booking BOOKED + tickets ISSUED
     * - confirms reservation with event-service (optional, stub for now)
     */
    @Transactional
    public String mockPay(Integer bookingId, String userEmail) {
        validateId(bookingId);

        UserSummary user = userClient.getUserByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        if (!user.id().equals(booking.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized user");
        }

        // Prevent double payment (adjust based on your PaymentRepository)
        if (paymentRepository.existsByBookingId(bookingId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Booking already paid");
        }

        List<Ticket> bookingTickets = ticketRepository.findByBookingId(bookingId);
        if (bookingTickets.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No tickets found for booking");
        }

        BigDecimal totalAmount = bookingTickets.stream()
                .map(Ticket::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Create payment record locally
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setUserId(booking.getUserId());
        payment.setAmount(totalAmount);
        payment.setStatus(Payment.PaymentStatus.PAID);
        payment.setPaymentMethod(Payment.PaymentMethod.CREDIT_CARD);
        paymentRepository.save(payment);

        // Update local statuses
        booking.setStatus(Booking.BookingStatus.BOOKED);
        bookingTickets.forEach(t -> t.setStatus(Ticket.TicketStatus.ISSUED));
        ticketRepository.saveAll(bookingTickets);
        bookingRepository.save(booking);

        // Confirm reservation on event-service (stub if not implemented)
        eventClient.confirmSeats(booking.getEventId(), bookingId);

        // Create notification (no User entity)
        notificationService.createNotification(
                booking.getUserId(),
                booking.getId(),
                "Your payment was successful! Your booking is confirmed.");

        return "PAID";
    }

    private void validateId(Integer id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid ID");
        }
    }
}