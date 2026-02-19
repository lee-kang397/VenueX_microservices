package com.example.auth_service.service;

import java.util.List;
import java.util.stream.Collectors;


import com.example.auth_service.DTOs.UserResponseDTO;
import com.example.auth_service.controller.request.PasswordChangeRequest;
import com.example.auth_service.controller.request.RegisterRequest;
import com.example.auth_service.controller.request.UserUpdateRequest;
import com.example.auth_service.entities.Role;
import com.example.auth_service.entities.User;
import com.example.auth_service.repository.RoleRepository;
import com.example.auth_service.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // CREATE
    public User registerNewUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());

        // Secure the password from the DTO
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        // Assign default role
        Role userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new RuntimeException("Default USER role not found"));

        user.getRoles().add(userRole);

        return userRepository.save(user);
    }

    // READ (by id)
    public UserResponseDTO getUserById(Integer id, String requesterEmail, String requesterRole) {
        // Find the user or throw 404
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Logic Check: (Self) OR (Admin/SuperUser)
        boolean isSelf = user.getEmail().equals(requesterEmail);
        boolean isAdmin = "ADMIN".equals(requesterRole) || "SUPER_USER".equals(requesterRole);

        if (!isSelf && !isAdmin) {
            throw new RuntimeException("Access Denied: You cannot view other users' profiles.");
        }

        // Return the clean DTO
        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone()
        );
    }

    // READ (all)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponseDTO(
                        user.getId(),
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getPhone()
                ))
                .collect(Collectors.toList());
    }

    // SEARCH
    public List<UserResponseDTO> searchUsers(String query) {
        // We search across email, first name, and last name
        return userRepository.findByEmailContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(query, query, query)
                .stream()
                .map(user -> new UserResponseDTO(
                        user.getId(),
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getPhone()
                ))
                .collect(Collectors.toList());
    }

    // UPDATE
    public User updateUser(Integer id, UserUpdateRequest request, String requesterEmail, String requesterRole) {
        // Fetch existing user
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Security Check: Is this the owner OR an admin?
        boolean isOwner = existing.getEmail().equals(requesterEmail);
        boolean isAdmin = "ADMIN".equals(requesterRole) || "SUPER_USER".equals(requesterRole);

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("Access Denied: You cannot update this profile.");
        }

        // Partial Update & Uniqueness Guard: Only validate/update if an email is provided (null check) AND it differs from the current one.
        // This prevents NullPointerExceptions and avoids users being blocked by their own existing email.
        if (request.getEmail() != null && !existing.getEmail().equalsIgnoreCase(request.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("The email " + request.getEmail() + " is already in use.");
            }
            existing.setEmail(request.getEmail());
        }

        // Attribute Guards: Only overwrite fields if the request provides a non-null value.
        // This allows for "Patch" style updates where unchanged data is preserved.
        if (request.getFirstName() != null) {
            existing.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            existing.setLastName(request.getLastName());
        }
        if (request.getPhone() != null) {
            existing.setPhone(request.getPhone());
        }

        // Save and return (ID remains the same)
        return userRepository.save(existing);
    }

    // UPDATE PASSWORD
    public void changePassword(Integer id, PasswordChangeRequest request, String requesterEmail) {
        // Fetch user
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Security: Ensure the requester is the owner of this specific ID
        if (!user.getEmail().equals(requesterEmail)) {
            throw new RuntimeException("Access Denied: You can only change your own password.");
        }

        // Verify the old password matches the hash in the DB
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new RuntimeException("The current password you entered is incorrect.");
        }

        // Encode the new password and save
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    // DELETE
    public void deleteUser(Integer id, String requesterEmail, String requesterRole) {
        // Find the user
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Security Check: Is it the owner or an admin?
        boolean isOwner = user.getEmail().equals(requesterEmail);
        boolean isAdmin = "ADMIN".equals(requesterRole) || "SUPER_USER".equals(requesterRole);

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("Access Denied: You do not have permission to delete this account.");
        }

        // Optional: Clean up roles or relationships if not handled by CascadeType.REMOVE
        user.getRoles().clear();

        // Delete from the database
        userRepository.delete(user);
    }

}