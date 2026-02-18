package com.venuex.transaction_service.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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

    private final TicketRepository ticketRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;
    private final EventClient eventClient;

    public TicketService(
            TicketRepository ticketRepository,
            BookingRepository bookingRepository,
            PaymentRepository paymentRepository,
            NotificationService notificationService,
            EventClient eventClient) {

        this.ticketRepository = ticketRepository;
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.notificationService = notificationService;
        this.eventClient = eventClient;
    }

    // ==========================================
    // GET TICKETS
    // ==========================================
    public List<TicketReturnDTO> getTicketsForBooking(Integer bookingId, Integer userId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        authorize(booking, userId);

        return ticketRepository.findByBookingId(bookingId)
                .stream()
                .map(ticket -> new TicketReturnDTO(
                        ticket.getId(),
                        ticket.getSeatSectionType(),
                        ticket.getPrice()))
                .collect(Collectors.toList());
    }

    // ==========================================
    // ADD TICKETS
    // ==========================================
    public BookingDTO addTicketsToBooking(
            Integer bookingId,
            List<TicketDTO> tickets,
            Integer userId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        authorize(booking, userId);

        // Prevent adding after payment
        if (paymentRepository.findByBookingId(bookingId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Booking already paid");
        }

        // Call event-service to reserve seats
        var reservationItems = tickets.stream()
                .map(t -> new EventClient.EventReservationItem(
                        t.getSeatSectionName(),
                        t.getQuantity()))
                .toList();

        var reservationResult = eventClient.reserveSeats(booking.getEventId(), reservationItems);

        BigDecimal total = BigDecimal.ZERO;

        for (var reserved : reservationResult.items()) {
            for (int i = 0; i < reserved.quantity(); i++) {

                Ticket ticket = new Ticket();
                ticket.setBooking(booking);
                ticket.setSeatSectionType(reserved.seatSectionType());
                ticket.setPrice(reserved.unitPrice());

                ticketRepository.save(ticket);
                total = total.add(reserved.unitPrice());
            }
        }

        return new BookingDTO(
                booking.getId(),
                booking.getUserEmail(),
                booking.getEventName(),
                booking.getBookedAt(),
                total);
    }

    // ==========================================
    // MOCK PAYMENT
    // ==========================================
    public String mockPay(Integer bookingId, Integer userId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        authorize(booking, userId);

        if (paymentRepository.findByBookingId(bookingId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already paid");
        }

        List<Ticket> tickets = ticketRepository.findByBookingId(bookingId);

        if (tickets.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No tickets found");
        }

        BigDecimal total = tickets.stream()
                .map(Ticket::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setUserId(userId);
        payment.setAmount(total);
        payment.setStatus(Payment.PaymentStatus.PAID);
        payment.setPaymentMethod(Payment.PaymentMethod.CREDIT_CARD);

        paymentRepository.save(payment);

        // Confirm seats in event-service
        eventClient.confirmSeats(
                booking.getEventId(),
                booking.getId());

        booking.setStatus(Booking.BookingStatus.BOOKED);
        bookingRepository.save(booking);

        notificationService.createNotification(
                booking.getId(),
                "Your payment was successful! Your booking is confirmed.");

        return "PAID";
    }

    // ==========================================
    // AUTHORIZATION
    // ==========================================
    private void authorize(Booking booking, Integer userId) {
        if (!booking.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unauthorized user");
        }
    }
}
