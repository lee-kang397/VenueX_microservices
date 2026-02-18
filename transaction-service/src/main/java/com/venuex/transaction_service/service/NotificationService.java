package com.venuex.transaction_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.venuex.transaction_service.DTO.NotificationDTO;
import com.venuex.transaction_service.entities.Notification;
import com.venuex.transaction_service.repository.NotificationRepository;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // ==============================
    // GET NOTIFICATIONS BY BOOKING
    // ==============================
    public List<NotificationDTO> getNotificationsForBooking(Integer bookingId) {

        return notificationRepository.findByBookingIdOrderByCreatedAtDesc(bookingId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ==============================
    // CREATE NOTIFICATION
    // ==============================
    public Notification createNotification(Integer bookingId, String message) {

        if (bookingId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking ID required");
        }

        if (message == null || message.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message required");
        }

        Notification notification = new Notification();
        notification.setBookingId(bookingId);
        notification.setMessage(message);
        notification.setStatus(Notification.NotificationStatus.PENDING);

        return notificationRepository.save(notification);
    }

    // ==============================
    // DELETE
    // ==============================
    public void deleteNotification(Integer id) {

        Notification existing = notificationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));

        notificationRepository.delete(existing);
    }

    // ==============================
    // MAPPER
    // ==============================
    private NotificationDTO mapToDTO(Notification n) {
        return new NotificationDTO(
                n.getId(),
                n.getBookingId(),
                n.getMessage(),
                n.getStatus(),
                n.getCreatedAt(),
                n.getSentAt());
    }
}
