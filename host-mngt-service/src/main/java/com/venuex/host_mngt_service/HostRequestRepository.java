package com.venuex.host_mngt_service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.venuex.host_mngt_service.HostRequest.HostRequestStatus;
import com.venuex.host_mngt_service.user.UserResponseDTO;

public interface HostRequestRepository extends JpaRepository<HostRequest, Integer> {
    boolean existsByUserAndStatus(UserResponseDTO user, HostRequestStatus status);
}
