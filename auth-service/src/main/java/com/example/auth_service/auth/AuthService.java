package com.example.auth_service.auth;


import com.example.auth_service.controller.request.LoginRequest;
import com.example.auth_service.controller.request.RegisterRequest;
import com.example.auth_service.entities.Role;
import com.example.auth_service.entities.User;
import com.example.auth_service.repository.UserRepository;
import com.example.auth_service.security.JwtUtil;
import com.example.auth_service.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil,
                       PasswordEncoder passwordEncoder, UserService userService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    public AuthResponse register(RegisterRequest registerRequest) {
        // Create the user using our existing userService logic
        User newUser = userService.registerNewUser(registerRequest);

        // Generate the token
        String token = jwtUtil.generateToken(newUser.getEmail(), "USER", newUser.getId());

        // Return the full response
        return new AuthResponse(newUser.getId(), token, "USER", newUser.getEmail(),
                newUser.getFirstName(), newUser.getLastName(), newUser.getPhone());
    }

    public AuthResponse authenticate(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        String primaryRole = getHighestRole(user);
        String token = jwtUtil.generateToken(user.getEmail(), primaryRole, user.getId());

        return new AuthResponse(user.getId(), token, primaryRole, user.getEmail(), user.getFirstName(),
                user.getLastName(), user.getPhone());
    }

    private String getHighestRole(User user) {
        List<String> priority = Arrays.asList("GUEST", "USER", "HOST", "ADMIN", "SUPER_USER");
        return user.getRoles().stream()
                .map(Role::getType)
                .max(Comparator.comparingInt(priority::indexOf))
                .orElse("USER");
    }
}