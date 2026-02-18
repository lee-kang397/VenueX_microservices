package com.venuex.transaction_service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.venuex.transaction_service.DTO.BookingDTO;
import com.venuex.transaction_service.DTO.TicketDTO;
import com.venuex.transaction_service.DTO.TicketReturnDTO;
import com.venuex.transaction_service.entities.Booking;
import com.venuex.transaction_service.entities.Payment;
import com.venuex.transaction_service.entities.Ticket;
import com.venuex.transaction_service.feign.EventClient;
import com.venuex.transaction_service.repository.BookingRepository;
import com.venuex.transaction_service.repository.PaymentRepository;
import com.venuex.transaction_service.repository.TicketRepository;

@Service
public class TicketService {

    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final PaymentRepository paymentRepository;
    private final EventClient eventClient;

    public TicketService(
            BookingRepository bookingRepository,
            TicketRepository ticketRepository,
            PaymentRepository paymentRepository,
            EventClient eventClient) {

        this.bookingRepository = bookingRepository;
        this.ticketRepository = ticketRepository;
        this.paymentRepository = paymentRepository;
        this.eventClient = eventClient;
    }

    // ---------------------------------------------------
    // ADD TICKETS
    // ---------------------------------------------------
    @Transactional
    public BookingDTO addTicketsToBooking(
            Integer bookingId,
            List<TicketDTO> tickets,
            Integer userId) {

        Booking booking = getOwnedBookingOrThrow(bookingId, userId);

        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Tickets can only be added to a PENDING booking.");
        }

        if (tickets == null || tickets.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ticket list cannot be empty.");
        }

        // Convert DTO → Feign reservation request
        List<EventClient.EventReservationItem> items = tickets.stream()
                .map(dto -> new EventClient.EventReservationItem(
                        dto.getSeatSectionName(),
                        dto.getQuantity()))
                .toList();

        // Call event-service to reserve seats
        EventClient.EventReservationResult reservation = eventClient.reserveSeats(booking.getEventId(), items);

        if (reservation == null || reservation.items().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No seats reserved.");
        }

        // Convert reserved seats → local tickets
        for (EventClient.ReservedItem r : reservation.items()) {

            for (int i = 0; i < r.quantity(); i++) {

                Ticket ticket = new Ticket();
                ticket.setSeatSectionType(r.seatSectionType());
                ticket.setPrice(r.unitPrice());
                ticket.setStatus(Ticket.TicketStatus.HELD);
                ticket.setCreatedAt(LocalDateTime.now());

                booking.addTicket(ticket);
            }
        }

        Booking saved = bookingRepository.save(booking);

        BigDecimal total = saved.getTickets().stream()
                .map(Ticket::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new BookingDTO(
                saved.getId(),
                saved.getUserEmail(),
                saved.getEventName(),
                saved.getBookedAt(),
                total);
    }

    // ---------------------------------------------------
    // GET TICKETS
    // ---------------------------------------------------
    @Transactional(readOnly = true)
    public List<TicketReturnDTO> getTicketsForBooking(
            Integer bookingId,
            Integer userId) {

        getOwnedBookingOrThrow(bookingId, userId);

        return ticketRepository.findByBookingId(bookingId)
                .stream()
                .map(ticket -> new TicketReturnDTO(
                        ticket.getId(),
                        ticket.getSeatSectionType(),
                        ticket.getPrice()))
                .toList();
    }

    // ---------------------------------------------------
    // MOCK PAYMENT
    // ---------------------------------------------------
    @Transactional
    public String mockPay(Integer bookingId, Integer userId) {

        Booking booking = getOwnedBookingOrThrow(bookingId, userId);

        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Booking is not payable.");
        }

        if (booking.getTickets().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No tickets to pay for.");
        }

        if (paymentRepository.existsByBookingId(bookingId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Payment already completed.");
        }

        BigDecimal total = booking.getTickets().stream()
                .map(Ticket::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setUserId(userId);
        payment.setAmount(total);
        payment.setPaymentMethod(Payment.PaymentMethod.CREDIT_CARD);
        payment.setStatus(Payment.PaymentStatus.PAID);
        payment.setTransactionRef("MOCK-" + UUID.randomUUID());
        payment.setCreatedAt(LocalDateTime.now());

        paymentRepository.save(payment);

        // Confirm seats in event-service
        eventClient.confirmSeats(
                booking.getEventId(),
                booking.getId());

        // Issue tickets
        booking.getTickets()
                .forEach(t -> t.setStatus(Ticket.TicketStatus.ISSUED));

        booking.setStatus(Booking.BookingStatus.BOOKED);
        bookingRepository.save(booking);

        return "Payment successful. Booking confirmed.";
    }

    // ---------------------------------------------------
    // HELPER
    // ---------------------------------------------------
    private Booking getOwnedBookingOrThrow(
            Integer bookingId,
            Integer userId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Booking not found."));

        if (!userId.equals(booking.getUserId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Unauthorized.");
        }

        return booking;
    }
}