package com.example.auth_service.controller;

import java.util.List;


import com.example.auth_service.DTOs.UserResponseDTO;
import com.example.auth_service.auth.AuthResponse;
import com.example.auth_service.controller.request.PasswordChangeRequest;
import com.example.auth_service.controller.request.UserUpdateRequest;
import com.example.auth_service.entities.Role;
import com.example.auth_service.entities.User;
import com.example.auth_service.security.JwtUtil;
import com.example.auth_service.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    // GET ALL USERS
    @GetMapping("/admin/users")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {

        // Get the list from the service
        List<UserResponseDTO> users = userService.getAllUsers();

        return ResponseEntity.ok(users);
    }

    // READ ONE
    @GetMapping("/user/{id}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable Integer id, HttpServletRequest request) {
        // Extract credentials from request attributes
        String requesterEmail = request.getHeader("X-User-Email");
        String requesterRole = request.getHeader("X-User-Role");


        // Pass these to the service so it can decide if the user is allowed to see this
        UserResponseDTO user = userService.getUserById(id, requesterEmail, requesterRole);

        return ResponseEntity.ok(user);
    }

    //SEARCH BY EMAIL, FIRST, OR LAST NAME
    @GetMapping("/admin/users/search")
    public ResponseEntity<List<UserResponseDTO>> searchUsers(@RequestParam String query) {

        // Call service with the search term
        List<UserResponseDTO> results = userService.searchUsers(query);

        return ResponseEntity.ok(results);
    }

    // UPDATE
    @PutMapping("/user/{id}")
    public ResponseEntity<AuthResponse> updateProfile(
            @PathVariable Integer id,
            @RequestBody UserUpdateRequest dto,
            HttpServletRequest request) {

        // Get the identity of the person making the request
        String requesterEmail = request.getHeader("X-User-Email");
        String requesterRole = request.getHeader("X-User-Role");


        // Call the service to update the database
        User updatedUser = userService.updateUser(id, dto, requesterEmail, requesterRole);

        // Since the email might have changed, we MUST generate a new token
        String primaryRole = updatedUser.getRoles().stream()
                .map(Role::getType)
                .findFirst()
                .orElse("USER");

        String newToken = jwtUtil.generateToken(updatedUser.getEmail(), primaryRole, updatedUser.getId());

        // Return the new token so the frontend can stay logged in
        return ResponseEntity.ok(new AuthResponse(updatedUser.getId(), newToken, primaryRole, updatedUser.getEmail(),
                updatedUser.getFirstName(), updatedUser.getLastName(), updatedUser.getPhone()));
    }

    // UPDATE PASSWORD
    @PutMapping("/user/{id}/password")
    public ResponseEntity<String> updatePassword(
            @PathVariable Integer id,
            @RequestBody PasswordChangeRequest request,
            HttpServletRequest HttpRequest) {

        // Get the email of the person currently logged in from the JWT filter
        String requesterEmail = HttpRequest.getHeader("X-User-Email");

        // Pass everything to the service
        userService.changePassword(id, request, requesterEmail);

        return ResponseEntity.ok("Password updated successfully");
    }

    // DELETE
    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id, HttpServletRequest request) {
        // Identify who is making the request
        String requesterEmail = request.getHeader("X-User-Email");
        String requesterRole = request.getHeader("X-User-Role");

        // Execute the deletion logic
        userService.deleteUser(id, requesterEmail, requesterRole);

        // Return 204 No Content (standard for successful deletes)
        return ResponseEntity.noContent().build();
    }
}