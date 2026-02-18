package com.venuex.host_mngt_service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.venuex.host_mngt_service.HostRequest.HostRequestStatus;
import com.venuex.host_mngt_service.notification.NotificationServiceClient;
import com.venuex.host_mngt_service.user.UserResponseDTO;
import com.venuex.host_mngt_service.user.UserServiceClient;

import jakarta.transaction.Transactional;

@Service
public class HostService {

    private final HostRequestRepository hostRequestRepository;
    private final UserServiceClient userServiceClient;
    private final NotificationServiceClient notificationServiceClient;

    @Autowired
    public HostService(
        HostRequestRepository hostRequestRepository, 
        UserServiceClient userServiceClient, 
        NotificationServiceClient notificationServiceClient) {
        this.hostRequestRepository = hostRequestRepository;
        this.userServiceClient = userServiceClient;
        this.notificationServiceClient = notificationServiceClient;
    }

    //MAP TO DTO
    public HostRequestDTO mapToDTO(HostRequest hostRequest) {
        HostRequestDTO hostRequestDTO = new HostRequestDTO();
        hostRequestDTO.setId(hostRequest.getId());
        hostRequestDTO.setUserId(hostRequest.getUserId());
        hostRequestDTO.setRequestedTime(hostRequest.getRequestedTime());

        return hostRequestDTO;
    }

    //CREATE
    public String createHostRequest(Integer userId, String role) {
        if (!"USER".equals(role)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not a user");
        }
        UserResponseDTO user = userServiceClient.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        //see if another request already exists 
        boolean exists = hostRequestRepository
            .existsByUserAndStatus(user, HostRequestStatus.PENDING);

        if (exists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Host request already pending");
        }
        HostRequest newRequest = new HostRequest();
        newRequest.setUserId(user.getId());
        newRequest.setStatus(HostRequestStatus.PENDING);
        hostRequestRepository.save(newRequest);
        return "SUBMITTED";
    }
 
    //Get all host requests 
    public List<HostRequestDTO> getAllHostRequests(String role) {
        if (!"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not an Admin");
        }
        return hostRequestRepository.findAll()
            .stream()
            .map(this::mapToDTO)
            .toList();
    }

    @Transactional
    public void approveHostRequest(Integer requestId, Integer adminId, String role) {
        UserResponseDTO admin = userServiceClient.findById(adminId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));

        if (!"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not an Admin");
        }

        HostRequest request = hostRequestRepository.findById(requestId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Host request not found"));

        if (request.getStatus() != HostRequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request already processed");
        }

        // Update request
        request.setStatus(HostRequestStatus.APPROVED);
        request.setReviewedBy(admin.getId());

        //!!! dont work rn, will fix !!!
        // Promote user to HOST
        UserResponseDTO user = userServiceClient.findById(request.getUserId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        hostRequestRepository.save(request);
        userServiceClient.updateUserRole(request.getUserId(), "HOST");
        notificationServiceClient.createNotification(
            user,
            "Congratulations, Your request to be a host has been approved!");
    }

    @Transactional
    public void denyHostRequest(Integer requestId, Integer adminId, String role) {
        UserResponseDTO admin = userServiceClient.findById(adminId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found"));

        if (!"ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not an Admin");
        }

        HostRequest request = hostRequestRepository.findById(requestId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Host request not found"));

        if (request.getStatus() != HostRequestStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request already processed");
        }

        // Update request
        request.setStatus(HostRequestStatus.DENIED);
        request.setReviewedBy(admin.getId());
        hostRequestRepository.save(request);

        UserResponseDTO user = userServiceClient.findById(request.getUserId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        notificationServiceClient.createNotification(
            user,
            "We apologize, Your request to be a host has been denied!");
    }

    public void deleteHostRequest(Integer id, Integer userId, String role) {

        HostRequest request = hostRequestRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Host request not found"));

        boolean isCreator = request.getUserId().equals(userId);
        boolean isAdmin = role.equals("ADMIN");
        if (!isCreator && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to delete this host request");
        }
        hostRequestRepository.delete(request);
    }
}
