package com.venuex.transaction_service.controller;

import com.venuex.transaction_service.DTO.NotificationDTO;
import com.venuex.transaction_service.DTO.UserSummary;
import com.venuex.transaction_service.service.NotificationService;
import com.venuex.transaction_service.service.feign.UserClient;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserClient userClient;

    public NotificationController(NotificationService notificationService, UserClient userClient) {
        this.notificationService = notificationService;
        this.userClient = userClient;
    }

    private String requireUserEmail(HttpServletRequest request) {
        Object val = request.getAttribute("userEmail");
        if (val == null || ((String) val).isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing user identity");
        }
        return (String) val;
    }

    @GetMapping("/notifications")
    @ResponseStatus(HttpStatus.OK)
    public List<NotificationDTO> getAllNotifications(HttpServletRequest request) {
        String userEmail = requireUserEmail(request);

        UserSummary user = userClient.getUserByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return notificationService.getUserNotifications(user.id());
    }

    @DeleteMapping("/notifications/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteNotification(@PathVariable Integer id, HttpServletRequest request) {
        String userEmail = requireUserEmail(request);

        UserSummary user = userClient.getUserByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        notificationService.deleteNotification(id, user.id());
    }
}