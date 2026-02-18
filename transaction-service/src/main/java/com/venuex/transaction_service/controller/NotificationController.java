package com.venuex.transaction_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.venuex.transaction_service.DTO.NotificationDTO;
import com.venuex.transaction_service.service.NotificationService;

@RestController
@RequestMapping("/api")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // ==============================
    // GET NOTIFICATIONS
    // ==============================
    @GetMapping("/user/bookings/{bookingId}/notifications")
    @ResponseStatus(HttpStatus.OK)
    public List<NotificationDTO> getNotifications(
            @PathVariable Integer bookingId) {

        return notificationService.getNotificationsForBooking(bookingId);
    }

    // ==============================
    // DELETE
    // ==============================
    @DeleteMapping("/user/notifications/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteNotification(@PathVariable Integer id) {
        notificationService.deleteNotification(id);
    }
}