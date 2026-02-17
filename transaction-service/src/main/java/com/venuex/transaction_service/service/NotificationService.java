package com.venuex.transaction_service.service;

import com.venuex.transaction_service.DTO.NotificationDTO;
import com.venuex.transaction_service.entities.Notification;
import com.venuex.transaction_service.entities.Notification.NotificationStatus;
import com.venuex.transaction_service.entities.Notification.NotificationType;
import com.venuex.transaction_service.repository.NotificationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    private NotificationDTO toDto(Notification n) {
        return new NotificationDTO(
                n.getId(),
                n.getUserId(),
                n.getBookingId(),
                n.getType().name(),
                n.getStatus().name(),
                n.getMessage(),
                n.getCreatedAt(),
                n.getSentAt());
    }

    /**
     * Get notifications for a user (no user-service join).
     * Caller should provide userId (recommended) or you can resolve by email via a
     * UserClient.
     */
    @Transactional(readOnly = true)
    public List<NotificationDTO> getUserNotifications(Integer userId) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        }

        // Recommended repo method:
        // List<Notification> findByUserIdOrderByCreatedAtDesc(Integer userId);
        List<Notification> notifications = notificationRepository.findByUserIdOrderBySentAtDesc(userId);

        return notifications.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Creates a notification in PENDING state (outbox style).
     * Another job/worker can mark it SENT later.
     */
    @Transactional
    public NotificationDTO createNotification(Integer userId, Integer bookingId, String message) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        }
        if (message == null || message.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message cannot be empty");
        }

        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setBookingId(bookingId);
        notification.setType(NotificationType.EMAIL);
        notification.setStatus(NotificationStatus.PENDING);
        notification.setMessage(message);

        // If your entity has @PrePersist for createdAt, you can omit these:
        if (notification.getCreatedAt() == null) {
            notification.setCreatedAt(LocalDateTime.now());
        }

        Notification saved = notificationRepository.save(notification);
        return toDto(saved);
    }

    /**
     * Deletes a notification only if it belongs to the userId (no email join).
     */
    @Transactional
    public void deleteNotification(Integer notificationId, Integer userId) {
        if (notificationId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "notificationId is required");
        }
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        }

        Notification existing = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));

        if (!userId.equals(existing.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User not authorized to delete this notification");
        }

        notificationRepository.delete(existing);
    }

    /**
     * Worker-style method: mark as SENT (or FAILED) after sending.
     * This is how you demo notifications reliably.
     */
    @Transactional
    public void markSent(Integer notificationId) {
        Notification existing = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));

        existing.setStatus(NotificationStatus.SENT);
        existing.setSentAt(LocalDateTime.now());
        notificationRepository.save(existing);
    }

    @Transactional
    public void markFailed(Integer notificationId) {
        Notification existing = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));

        existing.setStatus(NotificationStatus.FAILED);
        notificationRepository.save(existing);
    }
}