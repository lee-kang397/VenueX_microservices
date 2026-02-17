package com.venuex.transaction_service.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.venuex.transaction_service.entities.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    Optional<Payment> findByBookingId(Integer bookingId);

    boolean existsByBookingId(Integer bookingId);

}
