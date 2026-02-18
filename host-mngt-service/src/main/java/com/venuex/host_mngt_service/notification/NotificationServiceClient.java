package com.venuex.host_mngt_service.notification;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.venuex.host_mngt_service.user.UserResponseDTO;

@FeignClient(name = "notification-service", path = "/api/user/notifications")
public interface NotificationServiceClient {

    //no endpoint
    @PostMapping
    NotificationDTO createNotification(@RequestParam("user") UserResponseDTO user, @RequestParam("message") String message);

    
}
