package com.venuex.host_mngt_service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.venuex.host_mngt_service.HostRequest.HostRequestStatus;
import com.venuex.host_mngt_service.notification.NotificationService;
import com.venuex.host_mngt_service.user.Role;
import com.venuex.host_mngt_service.user.RoleRepository;
import com.venuex.host_mngt_service.user.User;
import com.venuex.host_mngt_service.user.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class HostService {

    private final HostRequestRepository hostRequestRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final NotificationService notificationService;

    @Autowired
    public HostService(
        HostRequestRepository hostRequestRepository, 
        UserRepository userRepository, 
        RoleRepository roleRepository,
        NotificationService notificationService) {
        this.hostRequestRepository = hostRequestRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.notificationService = notificationService;
    }

    //MAP TO DTO
    public HostRequestDTO mapToDTO(HostRequest hostRequest) {
        HostRequestDTO hostRequestDTO = new HostRequestDTO();
        hostRequestDTO.setId(hostRequest.getId());
        hostRequestDTO.setUserId(hostRequest.getUser().getId());
        hostRequestDTO.setRequestedTime(hostRequest.getRequestedTime());

        return hostRequestDTO;
    }

    //CREATE
    public String createHostRequest(String userEmail, String role) {
        if (!"USER".equals(role)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not a user");
        }
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        //see if another request already exists 
        boolean exists = hostRequestRepository
            .existsByUserAndStatus(user, HostRequestStatus.PENDING);

        if (exists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Host request already pending");
        }
        HostRequest newRequest = new HostRequest();
        newRequest.setUser(user);
        newRequest.setStatus(HostRequestStatus.PENDING);
        hostRequestRepository.save(newRequest);
        return "SUBMITTED";
    }
 
    //Get all host request 
    public List<HostRequestDTO> getAllHostRequests(String role) {
        if (!"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a Admin");
        }
        return hostRequestRepository.findAll()
            .stream()
            .map(this::mapToDTO)
            .toList();
    }

    @Transactional
    public void approveHostRequest(Integer requestId, String adminEmail, String role) {
        User admin = userRepository.findByEmail(adminEmail)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));

        if (!"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a Admin");
        }

        HostRequest request = hostRequestRepository.findById(requestId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Host request not found"));

        if (request.getStatus() != HostRequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request already processed");
        }

        // Update request
        request.setStatus(HostRequestStatus.APPROVED);
        request.setReviewedBy(admin);

        // Promote user to HOST
        User user = request.getUser();
        Role hostRole = roleRepository.findByRoleName("HOST")
            .orElseThrow(() -> new RuntimeException("HOST role not found"));
        user.getRoles().add(hostRole);

        hostRequestRepository.save(request);
        userRepository.save(user);
        notificationService.createNotification(
            user,
            "Congratulations, Your request to be a host has been approved!");
    }

    @Transactional
    public void denyHostRequest(Integer requestId, String adminEmail, String role) {
        User admin = userRepository.findByEmail(adminEmail)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));

        if (!"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a Admin");
        }

        HostRequest request = hostRequestRepository.findById(requestId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Host request not found"));

        if (request.getStatus() != HostRequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request already processed");
        }

        // Update request
        request.setStatus(HostRequestStatus.DENIED);
        request.setReviewedBy(admin);
        hostRequestRepository.save(request);

        User user = request.getUser();
        notificationService.createNotification(
            user,
            "We apologize, Your request to be a host has been denied!");
    }

    public void deleteHostRequest(Integer id, String userEmail, String role) {

        HostRequest request = hostRequestRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Host request not found"));

        boolean isCreator = request.getUser().getEmail().equals(userEmail);
        boolean isAdmin = role.equals("ADMIN");
        if (!isCreator && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete this host request");
        }
        hostRequestRepository.delete(request);
    }
}
