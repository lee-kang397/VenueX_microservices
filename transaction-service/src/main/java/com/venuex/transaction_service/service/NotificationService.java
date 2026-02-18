package com.venuex.transaction_service.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.venuex.transaction_service.DTO.NotificationDTO;
import com.venuex.transaction_service.entities.Notification;
import com.venuex.transaction_service.feign.UserClient;
import com.venuex.transaction_service.repository.NotificationRepository;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserClient userClient;

    public NotificationService(NotificationRepository notificationRepository, UserClient userClient) {
        this.notificationRepository = notificationRepository;
        this.userClient = userClient;
    }

    private NotificationDTO toDto(Notification n, String userName) {
        return new NotificationDTO(
                n.getId(),
                userName,
                n.getMessage(),
                n.getSentAt() // or createdAt if your DTO uses that
        );
    }

    public List<NotificationDTO> getUserNotifications(Integer userId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing/invalid user id");
        }

        UserClient.UserResponse user = userClient.getUserById(userId);
        String userName = user.fullName().isBlank() ? user.email() : user.fullName();

        List<Notification> notifications = notificationRepository.findByUserIdOrderBySentAtDesc(userId);

        return notifications.stream()
                .map(n -> toDto(n, userName))
                .toList();
    }

    public void deleteNotification(Integer notificationId, Integer userId) {
        if (notificationId == null || notificationId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid notification id");
        }

        Notification existing = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));

        if (!existing.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
        }

        notificationRepository.delete(existing);
    }
}