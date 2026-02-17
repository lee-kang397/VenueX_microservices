package com.venuex.transaction_service.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.venuex.transaction_service.entities.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    List<Ticket> findByBookingId(Integer bookingId);
}