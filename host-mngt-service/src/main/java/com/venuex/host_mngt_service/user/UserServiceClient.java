package com.venuex.host_mngt_service.user;

import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    // endpoint exists, untested
    @GetMapping("/user/{id}")
    Optional<UserResponseDTO> findById(
        @PathVariable("id") Integer id);

    // no endpoint
    @PatchMapping("/user/{id}/role")
    ResponseEntity<String> updateUserRole(
            @PathVariable("id") Integer userId,
            @RequestParam("role") String role);

}
