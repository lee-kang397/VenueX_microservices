package com.venuex.transaction_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
        String userIdHeader = request.getHeader("X-User-Id");

        if (userIdHeader == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "MISING X-User-Id header");
        }

        Integer userId;

        try {
            userId = Integer.parseInt(userIdHeader);
        } catch (NumberFormatException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid X-User-Id format");
        }

        return notificationService.getUserNotifications(userId);
    }

    @DeleteMapping("/user/notifications/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNotification(@PathVariable Integer id, HttpServletRequest request) {
        String userIdHeader = request.getHeader("X-User-ID");

        if (userIdHeader == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "MISING X-User-Id header");
        }

        Integer userId;

        try {
            userId = Integer.parseInt(userIdHeader);
        } catch (NumberFormatException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid X-User-Id format");
        }
        notificationService.deleteNotification(id, userId);
    }
}