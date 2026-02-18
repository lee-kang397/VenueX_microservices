package com.venuex.transaction_service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserClient {

    // Matches your user-service response JSON
    record UserResponse(
            Integer id,
            String email,
            String firstName,
            String lastName,
            String phone) {
        public String fullName() {
            String fn = firstName == null ? "" : firstName.trim();
            String ln = lastName == null ? "" : lastName.trim();
            return (fn + " " + ln).trim();
        }
    }

    @GetMapping("/api/users/{id}")
    UserResponse getUserById(@PathVariable("id") Integer id);
}