package com.example.auth_service.util;

import com.example.auth_service.entities.Role;
import com.example.auth_service.entities.User;
import com.example.auth_service.repository.RoleRepository;
import com.example.auth_service.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedUsers(UserRepository userRepo,
                                RoleRepository roleRepo,
                                PasswordEncoder encoder) {
        return args -> {

            Role superAdminRole = roleRepo.findByRoleName("SUPER_USER")
                    .orElseThrow(() -> new RuntimeException("SUPER_USER role not found"));

            Role adminRole = roleRepo.findByRoleName("ADMIN")
                    .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

            Role hostRole = roleRepo.findByRoleName("HOST")
                    .orElseThrow(() -> new RuntimeException("HOST role not found"));

            Role userRole = roleRepo.findByRoleName("USER")
                    .orElseThrow(() -> new RuntimeException("USER role not found"));

            createUserIfNotExists(userRepo, encoder,
                    "super@test.com", "password",
                    "Super", "User", "1111111111",
                    Set.of(superAdminRole));

            createUserIfNotExists(userRepo, encoder,
                    "admin@test.com", "password",
                    "Admin", "User", "2222222222",
                    Set.of(adminRole));

            createUserIfNotExists(userRepo, encoder,
                    "host@test.com", "password",
                    "Host", "User", "3333333333",
                    Set.of(hostRole));

            createUserIfNotExists(userRepo, encoder,
                    "user1@test.com", "password",
                    "User", "One", "4444444444",
                    Set.of(userRole));

            createUserIfNotExists(userRepo, encoder,
                    "user2@test.com", "password",
                    "User", "Two", "5555555555",
                    Set.of(userRole));

            createUserIfNotExists(userRepo, encoder,
                    "user3@test.com", "password",
                    "User", "Three", "6666666666",
                    Set.of(userRole));
        };
    }

    // 👇 helper method INSIDE same class
    private void createUserIfNotExists(UserRepository userRepo,
                                       PasswordEncoder encoder,
                                       String email,
                                       String rawPassword,
                                       String firstName,
                                       String lastName,
                                       String phone,
                                       Set<Role> roles) {

        if (userRepo.findByEmail(email).isEmpty()) {
            User user = new User();
            user.setEmail(email);
            user.setPasswordHash(encoder.encode(rawPassword));
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPhone(phone);
            user.setRoles(roles);

            userRepo.save(user);
        }
    }
}
