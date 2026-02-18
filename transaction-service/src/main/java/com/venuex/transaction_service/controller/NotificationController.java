package com.venuex.transaction_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.venuex.transaction_service.DTO.NotificationDTO;
import com.venuex.transaction_service.service.NotificationService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/user/notifications")
    @ResponseStatus(HttpStatus.OK)
    public List<NotificationDTO> getAllNotifications(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        return notificationService.getUserNotifications(userId);
    }

    @DeleteMapping("/user/notifications/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteNotification(@PathVariable Integer id, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        notificationService.deleteNotification(id, userId);
    }
}