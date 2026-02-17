package com.venuex.transaction_service.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.venuex.transaction_service.entities.Booking;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByUserId(Integer userId);

    List<Booking> findByUserIdAndStatus(Integer userId, Booking.BookingStatus status);
}